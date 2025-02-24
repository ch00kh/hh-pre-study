package site.ch00kh.domain.account.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.ch00kh.domain.account.dao.account.Account;
import site.ch00kh.domain.account.dao.account.AccountRepository;
import site.ch00kh.domain.account.dto.AccountDetails;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account userData = accountRepository.findByUsername(username);

        if(userData != null) {
            return new AccountDetails(userData);
        }

        return null;
    }
}
