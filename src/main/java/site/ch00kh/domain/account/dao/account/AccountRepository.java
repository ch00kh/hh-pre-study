package site.ch00kh.domain.account.dao.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByUsername(String username);

    // select username from user_entity where username = ?
    Account findByUsername(String username);

}
