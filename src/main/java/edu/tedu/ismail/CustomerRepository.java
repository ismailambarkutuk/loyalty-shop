package edu.tedu.ismail;

import java.util.HashMap;
import java.util.Map;

/**
 * Basit bir in-memory müşteri deposu.
 * Gerçek projede database olurdu, burada ödev için hafif bir yapı kullanıyoruz.
 */
public class CustomerRepository {

    private final Map<String, Customer> customers = new HashMap<>();

    public Customer findById(String id) {
        // müşteri var mı yok mu döner
        return customers.get(id);
    }

    public void save(Customer customer) {
        // yeni müşteri ekler
        customers.put(customer.getId(), customer);
    }

    public void addPoints(String id, int points) {
        // müşterinin puanını artırır
        Customer c = customers.get(id);
        if (c != null) {
            c.setLoyaltyPoints(c.getLoyaltyPoints() + points);
        }
    }



}
