package site.ch00kh.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.ch00kh.domain.account.dao.account.Account;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinResponseDto {

    private String username;
    private String password;

    public static JoinResponseDto from(Account account) {
        return new JoinResponseDto(account.getUsername(), account.getPassword());
    }
}
