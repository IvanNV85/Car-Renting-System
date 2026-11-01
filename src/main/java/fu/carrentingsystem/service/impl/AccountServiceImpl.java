package fu.carrentingsystem.service.impl;

import fu.carrentingsystem.constant.Role;
import fu.carrentingsystem.dto.request.RegisterRequest;
import fu.carrentingsystem.entity.Account;
import fu.carrentingsystem.entity.Customer;
import fu.carrentingsystem.repository.AccountRepository;
import fu.carrentingsystem.repository.CustomerRepository;
import fu.carrentingsystem.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Account registerCustomer(RegisterRequest request) {
        // 1. Create and save Account
        Account account = new Account();
        account.setAccountName(request.getAccountName().trim());
        account.setEmail(request.getEmail().trim().toLowerCase());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(Role.CUSTOMER);
        Account savedAccount = accountRepository.save(account);

        // 2. Create and save Customer profile linked to Account
        Customer customer = new Customer();
        customer.setFullName(request.getFullName().trim());
        customer.setMobile(request.getMobile().trim());
        customer.setBirthday(request.getBirthday());
        customer.setIdentityCard(request.getIdentityCard().trim());
        customer.setLicenceNumber(request.getLicenceNumber().trim());
        customer.setLicenceDate(request.getLicenceDate());
        customer.setAccount(savedAccount);

        customerRepository.save(customer);
        savedAccount.setCustomer(customer);

        return savedAccount;
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByAccountName(String accountName) {
        return accountRepository.existsByAccountName(accountName);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Optional<Account> findByAccountName(String accountName) {
        return accountRepository.findByAccountName(accountName);
    }
}
