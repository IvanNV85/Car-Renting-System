package fu.carrentingsystem.service;

import fu.carrentingsystem.dto.request.CustomerUpdateRequest;
import fu.carrentingsystem.dto.response.CustomerResponse;
import fu.carrentingsystem.entity.Customer;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CustomerService {
    boolean existsByIdentityCard(String identityCard);

    boolean existsByMobile(String mobile);

    boolean existsByLicenceNumber(String licenceNumber);

    Optional<Customer> findByAccountId(int accountId);

    Optional<Customer> findByEmail(String email);

    Page<CustomerResponse> getAllCustomers(int page, int size, String keyword);

    CustomerResponse getCustomerDetail(int customerId);

    CustomerUpdateRequest getCustomerUpdateForm(int customerId);

    void updateCustomer(int customerId, CustomerUpdateRequest request);

    void deleteCustomer(int customerId);
}
