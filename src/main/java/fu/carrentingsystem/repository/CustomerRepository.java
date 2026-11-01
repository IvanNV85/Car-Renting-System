package fu.carrentingsystem.repository;

import fu.carrentingsystem.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByAccount_AccountId(int accountId);

    Optional<Customer> findByAccount_Email(String email);

    boolean existsByIdentityCard(String identityCard);

    boolean existsByMobile(String mobile);

    boolean existsByLicenceNumber(String licenceNumber);

    boolean existsByIdentityCardAndCustomerIdNot(String identityCard, int customerId);

    boolean existsByMobileAndCustomerIdNot(String mobile, int customerId);

    boolean existsByLicenceNumberAndCustomerIdNot(String licenceNumber, int customerId);

    @Query("SELECT c FROM Customer c JOIN c.account a " +
            "WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.mobile) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.identityCard) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);
}
