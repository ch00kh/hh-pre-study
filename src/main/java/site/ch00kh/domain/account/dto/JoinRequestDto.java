package site.ch00kh.domain.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import site.ch00kh.domain.account.dao.account.Account;
import site.ch00kh.global.common.Role;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestDto {

    @NotBlank(message = "username은 필수 값입니다.")
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,15}$")
    private String password;

    public Account toAccount(String decryptedPassword) {
        return Account.builder()
                .username(this.getUsername())
                .password(decryptedPassword)
                .role(Role.USER.name())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
