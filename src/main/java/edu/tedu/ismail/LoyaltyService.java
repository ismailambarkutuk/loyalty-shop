package edu.tedu.ismail;

/**
 * Harcama miktarına göre puan hesaplayan ve repository üzerinden müşteriye puan ekleyen servis.
 */
public class LoyaltyService {

    private final CustomerRepository customerRepository;

    public LoyaltyService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Basit bir kural: her 10 TL için 1 puan.
     */
    public int calculatePointsForPurchase(double amount) {
        if (amount <= 0) {
            return 0;
        }
        return (int) (amount / 10.0);
    }

    /**
     * Satın alım için puan hesaplar ve ilgili müşteriye ekler.
     *
     * @return kazanılan puan
     */
    public int applyPoints(String customerId, double amount) {
        int points = calculatePointsForPurchase(amount);
        if (points > 0) {
            customerRepository.addPoints(customerId, points);
        }
        return points;
    }
}
