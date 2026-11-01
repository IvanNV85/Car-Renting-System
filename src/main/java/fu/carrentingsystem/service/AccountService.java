package fu.carrentingsystem.service;

import fu.carrentingsystem.dto.request.RegisterRequest;
import fu.carrentingsystem.entity.Account;

import java.util.Optional;

public interface AccountService {
    Account registerCustomer(RegisterRequest request);

    boolean existsByEmail(String email);

    boolean existsByAccountName(String accountName);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByAccountName(String accountName);
}
