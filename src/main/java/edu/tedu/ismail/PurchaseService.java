package edu.tedu.ismail;

/**
 * Alışveriş akışını yöneten servis.
 * - Müşteri var mı?
 * - Tutar pozitif mi?
 * - Puanları hesapla ve müşteriye ekle
 */
public class PurchaseService {

    private final CustomerRepository customerRepository;
    private final LoyaltyService loyaltyService;

    public PurchaseService(CustomerRepository customerRepository,
                           LoyaltyService loyaltyService) {
        this.customerRepository = customerRepository;
        this.loyaltyService = loyaltyService;
    }

    /**
     * Alışveriş işlemini gerçekleştirir.
     * Basit senaryo: sadece puan kazandırma ve mesaj döndürme.
     */
    public PurchaseResult purchase(String customerId, double amount) {
        if (amount <= 0) {
            return new PurchaseResult(false,
                    "Amount must be positive",
                    0);
        }

        Customer customer = customerRepository.findById(customerId);
        if (customer == null) {
            return new PurchaseResult(false,
                    "Customer not found: " + customerId,
                    0);
        }

        int earnedPoints = loyaltyService.applyPoints(customerId, amount);
        return new PurchaseResult(true,
                "Purchase completed for " + customer.getName(),
                earnedPoints);
    }
}
