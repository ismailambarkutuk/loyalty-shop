import edu.tedu.ismail.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PurchaseServicePurchaseResultPairTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoyaltyService loyaltyService;

    private PurchaseService purchaseService;

    private Customer ali;

    @BeforeEach
    void setUp() {
        purchaseService = new PurchaseService(customerRepository, loyaltyService);
        ali = new Customer("1", "Ali");

        // Stubbing basic behavior
        when(customerRepository.findById("1")).thenReturn(ali);
        when(loyaltyService.applyPoints("1", 100.0)).thenReturn(10);
    }

    @Test
    void purchaseResult_successScenario() {
        PurchaseResult result = purchaseService.purchase("1", 100.0);

        assertTrue(result.isSuccess());
        assertEquals(10, result.getEarnedPoints());
        assertEquals("Purchase completed for Ali", result.getMessage());
    }

    @Test
    void purchaseResult_negativeAmount() {
        PurchaseResult result = purchaseService.purchase("1", -10.0);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());
        assertEquals("Amount must be positive", result.getMessage());
    }

    @Test
    void purchaseResult_zeroAmount() {
        PurchaseResult result = purchaseService.purchase("1", 0.0);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getEarnedPoints());
    }

    @Test
    void purchaseResult_customerNotFound() {
        when(customerRepository.findById("X")).thenReturn(null);

        PurchaseResult result = purchaseService.purchase("X", 50.0);

        assertFalse(result.isSuccess());
        assertEquals("Customer not found: X", result.getMessage());
    }

    @Test
    void purchaseResult_messageContainsName() {
        PurchaseResult result = purchaseService.purchase("1", 100.0);
        assertTrue(result.getMessage().contains("Ali"));
    }

    @Test
    void purchaseResult_earnedPointsReflectsMockedLoyaltyService() {
        when(loyaltyService.applyPoints("1", 200.0)).thenReturn(20);

        PurchaseResult result = purchaseService.purchase("1", 200.0);

        assertEquals(20, result.getEarnedPoints());
    }

    @Test
    void purchaseResult_earnedPointsZeroWhenLoyaltyReturnsZero() {
        when(loyaltyService.applyPoints("1", 5.0)).thenReturn(0);

        PurchaseResult result = purchaseService.purchase("1", 5.0);

        assertEquals(0, result.getEarnedPoints());
    }

    @Test
    void purchaseResult_successReflectsCustomerStatus() {
        PurchaseResult result = purchaseService.purchase("1", 100.0);
        assertTrue(result.isSuccess());
    }

    @Test
    void purchaseResult_messageFormattingConsistency() {
        PurchaseResult result = purchaseService.purchase("1", 100.0);
        assertEquals("Purchase completed for Ali", result.getMessage());
    }

    @Test
    void purchaseResult_edgeCase_largeAmountFormatting() {
        when(loyaltyService.applyPoints("1", 9999.0)).thenReturn(999);

        PurchaseResult result = purchaseService.purchase("1", 9999.0);

        assertEquals(999, result.getEarnedPoints());
    }
}
