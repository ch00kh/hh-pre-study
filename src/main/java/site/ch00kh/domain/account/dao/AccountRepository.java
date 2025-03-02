package site.ch00kh.domain.account.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByUsername(String username);

    // select username from user_entity where username = ?
    Optional<Account> findByUsername(String username);

}
