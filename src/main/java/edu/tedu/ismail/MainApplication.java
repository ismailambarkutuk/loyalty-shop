package edu.tedu.ismail;

public class MainApplication {

    public static void main(String[] args) {
        // 1) Repository'yi oluştur
        CustomerRepository customerRepository = new CustomerRepository();

        // 2) Örnek bir müşteri oluştur ve kaydet
        Customer customer = new Customer("CUST-1", "İsmail");
        customerRepository.save(customer);

        // 3) LoyaltyService ve PurchaseService'i oluştur
        LoyaltyService loyaltyService = new LoyaltyService(customerRepository);
        PurchaseService purchaseService = new PurchaseService(customerRepository, loyaltyService);

        // 4) Bir alışveriş simüle et
        double amount = 120.0; // 120 TL
        PurchaseService.PurchaseResult result =
                purchaseService.purchase(customer.getId(), amount);

        // 5) Sonuçları ekrana yaz
        System.out.println("Purchase success: " + result.isSuccess());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Earned points: " + result.getEarnedPoints());
        System.out.println("Customer final points: "
                + customerRepository.findById(customer.getId()).getLoyaltyPoints());
    }
}