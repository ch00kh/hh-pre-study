package site.ch00kh.domain.account.application;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.ch00kh.domain.account.dao.account.Account;
import site.ch00kh.domain.account.dao.account.AccountRepository;
import site.ch00kh.domain.account.dto.JoinRequestDto;
import site.ch00kh.domain.account.dto.JoinResponseDto;
import site.ch00kh.global.common.Role;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public JoinResponseDto signup(JoinRequestDto dto) throws BadRequestException {


        if (accountRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("이미 사용중인 계정명입니다.");
        }
        String decryptedPassword = bCryptPasswordEncoder.encode(dto.getPassword());

        Account account = accountRepository.save(dto.toAccount(decryptedPassword));

        return JoinResponseDto.from(account);
    }
}
