package fu.carrentingsystem.security;

import fu.carrentingsystem.entity.Account;
import fu.carrentingsystem.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<Account> accountOpt = accountRepository.findByEmail(usernameOrEmail);
        if (accountOpt.isEmpty()) {
            accountOpt = accountRepository.findByAccountName(usernameOrEmail);
        }

        Account account = accountOpt.orElseThrow(() ->
                new UsernameNotFoundException("Không tìm thấy người dùng: " + usernameOrEmail));

        //store in UserDetails object
        return User.builder()
                .username(account.getEmail())
                .password(account.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + account.getRole().name())))
                .build();
    }
}
