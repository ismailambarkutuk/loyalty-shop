import edu.tedu.ismail.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerRepositoryCustomerPairTest {

    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository = new CustomerRepository();
    }

    @Test
    void saveThenFindById_returnsSameCustomerInstance() {
        Customer customer = new Customer("C1", "Ali");
        customerRepository.save(customer);

        Customer fromRepo = customerRepository.findById("C1");

        assertNotNull(fromRepo);
        assertSame(customer, fromRepo); // aynı nesne referansı
        assertEquals("Ali", fromRepo.getName());
        assertEquals("C1", fromRepo.getId());
    }

    @Test
    void findById_unknownId_returnsNull() {
        Customer fromRepo = customerRepository.findById("UNKNOWN");
        assertNull(fromRepo);
    }

    @Test
    void addPoints_singleCustomer_pointsIncreaseCorrectly(){
        Customer customer = new Customer("C2", "Veli");
        customerRepository.save(customer);

        assertEquals(0, customer.getLoyaltyPoints());

        customerRepository.addPoints("C2", 10);
        assertEquals(10, customer.getLoyaltyPoints());

        customerRepository.addPoints("C2", 5);
        assertEquals(15, customer.getLoyaltyPoints());
    }

    @Test
    void addPoints_multipleCustomers_isolatedPoints() {
        Customer c1 = new Customer("C3", "Ayşe");
        Customer c2 = new Customer("C4", "Fatma");

        customerRepository.save(c1);
        customerRepository.save(c2);

        customerRepository.addPoints("C3", 10);
        customerRepository.addPoints("C4", 20);

        assertEquals(10, c1.getLoyaltyPoints());
        assertEquals(20, c2.getLoyaltyPoints());
    }

    @Test
    void addPoints_negativePoints_decreaseLoyaltyPoints() {
        Customer customer = new Customer("C5", "Mehmet");
        customerRepository.save(customer);

        customerRepository.addPoints("C5", 30);
        assertEquals(30, customer.getLoyaltyPoints());

        // negatif puanla azaltma senaryosu
        customerRepository.addPoints("C5", -5);
        assertEquals(25, customer.getLoyaltyPoints());
    }

    @Test
    void addPoints_forUnknownCustomer_doesNothing() {
        // repository boş, bu id yok
        customerRepository.addPoints("UNKNOWN", 50);

        // herhangi bir exception olmamalı ve map boş kalmalı.
        assertNull(customerRepository.findById("UNKNOWN"));
    }
}