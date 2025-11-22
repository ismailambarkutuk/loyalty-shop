import edu.tedu.ismail.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PurchaseServiceCustomerRepoPairTest {

    // PurchaseService.purchase() → CustomerRepository.findById() ilişkisi düzgün çalışıyor mu?
    // PurchaseService gerçekten repository’den müşteri çekiyor mu?

    private CustomerRepository customerRepository; // REAL

    @Mock
    private LoyaltyService loyaltyService;         // MOCK

    private PurchaseService purchaseService;       // REAL

    private Customer c1;
    private Customer c2;
    private Customer c3;
    private Customer c4;
    private Customer c5;

    @BeforeEach
    void setUp() {
        customerRepository = new CustomerRepository();

        c1 = new Customer("C1", "Ali");
        c2 = new Customer("C2", "Veli");
        c3 = new Customer("C3", "Ayşe");
        c4 = new Customer("C4", "Fatma");
        c5 = new Customer("C5", "Mehmet");

        customerRepository.save(c1);
        customerRepository.save(c2);
        customerRepository.save(c3);
        customerRepository.save(c4);
        customerRepository.save(c5);

        purchaseService = new PurchaseService(customerRepository, loyaltyService);

        // 5 different when - thenReturn behavior
        when(loyaltyService.applyPoints("C1", 100.0)).thenReturn(10);
        when(loyaltyService.applyPoints("C1", 50.0)).thenReturn(5);
        when(loyaltyService.applyPoints("C2", 20.0)).thenReturn(2);
        when(loyaltyService.applyPoints("C3", 5.0)).thenReturn(0);
        when(loyaltyService.applyPoints("C4", 200.0)).thenReturn(20);
    }

    @Test
    void purchase_success_C1_100tl() {
        PurchaseResult result = purchaseService.purchase("C1", 100.0);

        assertTrue(result.isSuccess());
        assertEquals(10, result.getEarnedPoints());
        assertEquals("Ali", customerRepository.findById("C1").getName());

        verify(loyaltyService).applyPoints("C1", 100.0);
    }

    @Test
    void purchase_success_C1_50tl() {
        PurchaseResult result = purchaseService.purchase("C1", 50.0);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getEarnedPoints());

        verify(loyaltyService).applyPoints("C1", 50.0);
    }

    @Test
    void purchase_success_C2_20tl() {
        PurchaseResult result = purchaseService.purchase("C2", 20.0);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getEarnedPoints());

        verify(loyaltyService).applyPoints("C2", 20.0);
    }

    @Test
    void purchase_success_C3_5tl_zeroPoints() {
        PurchaseResult result = purchaseService.purchase("C3", 5.0);

        assertTrue(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());

        verify(loyaltyService).applyPoints("C3", 5.0);
    }

    @Test
    void purchase_success_C4_200tl_twentyPoints() {
        PurchaseResult result = purchaseService.purchase("C4", 200.0);

        assertTrue(result.isSuccess());
        assertEquals(20, result.getEarnedPoints());

        verify(loyaltyService).applyPoints("C4", 200.0);
    }

    @Test
    void purchase_zeroAmount_noLoyaltyCall() {
        PurchaseResult result = purchaseService.purchase("C1", 0.0);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());

        verify(loyaltyService, never()).applyPoints(anyString(), anyDouble());
    }

    @Test
    void purchase_negativeAmount_noLoyaltyCall() {
        PurchaseResult result = purchaseService.purchase("C1", -20.0);

        assertFalse(result.isSuccess());

        verify(loyaltyService, never()).applyPoints(anyString(), anyDouble());
    }

    @Test
    void purchase_unknownCustomer_noLoyaltyCall() {
        PurchaseResult result = purchaseService.purchase("UNKNOWN", 100.0);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());

        verify(loyaltyService, never()).applyPoints(anyString(), anyDouble());
    }

    @Test
    void purchase_multiplePurchases_C1() {
        purchaseService.purchase("C1", 50.0);
        purchaseService.purchase("C1", 100.0);

        verify(loyaltyService).applyPoints("C1", 50.0);
        verify(loyaltyService).applyPoints("C1", 100.0);
    }

    @Test
    void purchase_message_containsCustomerName() {
        PurchaseResult result = purchaseService.purchase("C5", 10.0);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Mehmet"));
    }
}