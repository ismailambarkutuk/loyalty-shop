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
class PurchaseServiceLoyaltyServicePairTest {

    @Mock
    private CustomerRepository customerRepository;  // MOCK

    private LoyaltyService loyaltyService;          // REAL
    private PurchaseService purchaseService;        // REAL

    private Customer c1;
    private Customer c2;
    private Customer c3;
    private Customer c4;
    private Customer c5;

    @BeforeEach
    void setUp() {
        // LoyaltyService gerçek ama içinde mock repository kullanıyor
        loyaltyService = new LoyaltyService(customerRepository);
        purchaseService = new PurchaseService(customerRepository, loyaltyService);

        c1 = new Customer("C1", "Ali");
        c2 = new Customer("C2", "Veli");
        c3 = new Customer("C3", "Ayşe");
        c4 = new Customer("C4", "Fatma");
        c5 = new Customer("C5", "Mehmet");

        // ⭐ CustomerRepository mock’u için en az 5 farklı when-thenReturn davranışı
        when(customerRepository.findById("C1")).thenReturn(c1);
        when(customerRepository.findById("C2")).thenReturn(c2);
        when(customerRepository.findById("C3")).thenReturn(c3);
        when(customerRepository.findById("C4")).thenReturn(null);
        when(customerRepository.findById("C5")).thenReturn(null);
    }

    @Test
    void purchase_success_C1_100tl_pointsAdded() {
        PurchaseResult result = purchaseService.purchase("C1", 100.0);

        assertTrue(result.isSuccess());
        assertEquals(10, result.getEarnedPoints()); // 100 / 10
        assertTrue(result.getMessage().contains("Ali"));

        verify(customerRepository).findById("C1");
        // LoyaltyService.applyPoints gerçek, puan hesaplayıp addPoints çağırmalı
        verify(customerRepository).addPoints("C1", 10);
    }

    @Test
    void purchase_success_C2_99_9tl_pointsFloor() {
        PurchaseResult result = purchaseService.purchase("C2", 99.9);

        assertTrue(result.isSuccess());
        assertEquals(9, result.getEarnedPoints());  // floor(99.9 / 10)

        verify(customerRepository).findById("C2");
        verify(customerRepository).addPoints("C2", 9);
    }

    @Test
    void purchase_success_C3_amountLessThan10_noPoints() {
        PurchaseResult result = purchaseService.purchase("C3", 5.0);

        assertTrue(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());
        assertTrue(result.getMessage().contains("Ayşe"));

        verify(customerRepository).findById("C3");
        // Puan 0 olduğu için addPoints çağrılmamalı
        verify(customerRepository, never()).addPoints(eq("C3"), anyInt());
    }

    @Test
    void purchase_zeroAmount_fails_noRepositoryCall() {
        PurchaseResult result = purchaseService.purchase("C1", 0.0);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());
        assertTrue(result.getMessage().contains("Amount must be positive"));

        // amount <= 0 ise CustomerRepository hiç kullanılmamalı
        verify(customerRepository, never()).findById(anyString());
        verify(customerRepository, never()).addPoints(anyString(), anyInt());
    }

    @Test
    void purchase_negativeAmount_fails_noRepositoryCall() {
        PurchaseResult result = purchaseService.purchase("C2", -50.0);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());

        verify(customerRepository, never()).findById(anyString());
        verify(customerRepository, never()).addPoints(anyString(), anyInt());
    }

    @Test
    void purchase_customerNotFound_C4() {
        PurchaseResult result = purchaseService.purchase("C4", 100.0);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());
        assertTrue(result.getMessage().contains("Customer not found"));

        verify(customerRepository).findById("C4");
        verify(customerRepository, never()).addPoints(anyString(), anyInt());
    }

    @Test
    void purchase_customerNotFound_C5() {
        PurchaseResult result = purchaseService.purchase("C5", 50.0);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());

        verify(customerRepository).findById("C5");
        verify(customerRepository, never()).addPoints(anyString(), anyInt());
    }

    @Test
    void purchase_multipleCalls_sameCustomer_C1() {
        purchaseService.purchase("C1", 20.0);  // 2 puan
        purchaseService.purchase("C1", 30.0);  // 3 puan

        // findById her çağrıda çalışmalı
        verify(customerRepository, times(2)).findById("C1");
        // LoyaltyService’e göre puanlar 2 ve 3 olmalı
        verify(customerRepository).addPoints("C1", 2);
        verify(customerRepository).addPoints("C1", 3);
    }
}
