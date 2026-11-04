package fu.carrentingsystem.service.impl;

import fu.carrentingsystem.dto.request.CustomerUpdateRequest;
import fu.carrentingsystem.dto.response.CustomerResponse;
import fu.carrentingsystem.entity.Customer;
import fu.carrentingsystem.repository.AccountRepository;
import fu.carrentingsystem.repository.CarRentalRepository;
import fu.carrentingsystem.repository.CustomerRepository;
import fu.carrentingsystem.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CarRentalRepository carRentalRepository;

    @Override
    public boolean existsByIdentityCard(String identityCard) {
        return customerRepository.existsByIdentityCard(identityCard);
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return customerRepository.existsByMobile(mobile);
    }

    @Override
    public boolean existsByLicenceNumber(String licenceNumber) {
        return customerRepository.existsByLicenceNumber(licenceNumber);
    }

    @Override
    public Optional<Customer> findByAccountId(int accountId) {
        return customerRepository.findByAccount_AccountId(accountId);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByAccount_Email(email);
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), size, Sort.by("customerId").descending());
        Page<Customer> customers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            customers = customerRepository.searchCustomers(keyword.trim(), pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }
        return customers.map(this::mapToResponse);
    }

    @Override
    public CustomerResponse getCustomerDetail(int customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));
        return mapToResponse(customer);
    }

    @Override
    public CustomerUpdateRequest getCustomerUpdateForm(int customerId) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));
        CustomerUpdateRequest req = new CustomerUpdateRequest();
        req.setFullName(c.getFullName());
        req.setMobile(c.getMobile());
        req.setBirthday(c.getBirthday());
        req.setIdentityCard(c.getIdentityCard());
        req.setLicenceNumber(c.getLicenceNumber());
        req.setLicenceDate(c.getLicenceDate());
        return req;
    }

    @Override
    @Transactional
    public void updateCustomer(int customerId, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));

        if (customerRepository.existsByIdentityCardAndCustomerIdNot(request.getIdentityCard().trim(), customerId)) {
            throw new IllegalArgumentException("Số CCCD/CMND đã được sử dụng bởi khách hàng khác.");
        }
        if (customerRepository.existsByMobileAndCustomerIdNot(request.getMobile().trim(), customerId)) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi khách hàng khác.");
        }
        if (customerRepository.existsByLicenceNumberAndCustomerIdNot(request.getLicenceNumber().trim(), customerId)) {
            throw new IllegalArgumentException("Số GPLX đã được sử dụng bởi khách hàng khác.");
        }

        customer.setFullName(request.getFullName().trim());
        customer.setMobile(request.getMobile().trim());
        customer.setBirthday(request.getBirthday());
        customer.setIdentityCard(request.getIdentityCard().trim());
        customer.setLicenceNumber(request.getLicenceNumber().trim());
        customer.setLicenceDate(request.getLicenceDate());

        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(int customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));

        if (carRentalRepository.existsByCustomer_CustomerId(customerId)) {
            throw new IllegalStateException("Không thể xóa khách hàng vì khách hàng này đã có dữ liệu giao dịch thuê xe.");
        }

        if (customer.getAccount() != null) {
            accountRepository.delete(customer.getAccount());
        } else {
            customerRepository.delete(customer);
        }
    }

    private CustomerResponse mapToResponse(Customer c) {
        CustomerResponse.CustomerResponseBuilder builder = CustomerResponse.builder()
                .customerId(c.getCustomerId())
                .fullName(c.getFullName())
                .mobile(c.getMobile())
                .birthday(c.getBirthday())
                .identityCard(c.getIdentityCard())
                .licenceNumber(c.getLicenceNumber())
                .licenceDate(c.getLicenceDate());

        if (c.getAccount() != null) {
            builder.accountId(c.getAccount().getAccountId())
                    .accountName(c.getAccount().getAccountName())
                    .email(c.getAccount().getEmail())
                    .role(c.getAccount().getRole() != null ? c.getAccount().getRole().name() : null);
        }
        return builder.build();
    }
}
