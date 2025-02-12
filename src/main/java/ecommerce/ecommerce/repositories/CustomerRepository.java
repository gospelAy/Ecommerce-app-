package ecommerce.ecommerce.repositories;

import ecommerce.ecommerce.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
//    @Query("SELECT c FROM Customer c WHERE c.otp = :otp")
//    Optional<Customer> findByOtp(@Param("otp") String otp);
//
    Optional<Customer> findByOtp(String otp);
}