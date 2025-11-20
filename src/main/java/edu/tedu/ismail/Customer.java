package edu.tedu.ismail;

/**
 * Müşteri varlığını temsil eden basit POJO.
 */
public class Customer {

    private final String id;
    private final String name;
    private int loyaltyPoints;

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
        this.loyaltyPoints = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                '}';
    }
}
