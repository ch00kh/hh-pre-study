package site.ch00kh.domain.account.application;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.ch00kh.domain.account.dao.Account;
import site.ch00kh.domain.account.dao.AccountRepository;
import site.ch00kh.domain.account.dao.JwtTokenRepository;
import site.ch00kh.domain.account.dto.JoinRequestDto;
import site.ch00kh.domain.account.dto.JoinResponseDto;
import site.ch00kh.domain.account.dto.JwtTokenRefreshDto;
import site.ch00kh.global.auth.JWTUtil;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;
    private final JwtTokenRepository jwtTokenRepository;


    public JoinResponseDto signup(JoinRequestDto dto) throws BadRequestException {

        if (accountRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("이미 사용중인 계정명입니다.");
        }
        String decryptedPassword = bCryptPasswordEncoder.encode(dto.getPassword());

        Account account = accountRepository.save(dto.toAccount(decryptedPassword));

        return JoinResponseDto.from(account);
    }

    public JwtTokenRefreshDto reissue(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            refreshToken = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("refreshToken"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        if (refreshToken == null) {
            return JwtTokenRefreshDto.error("Refresh Token이 없습니다.");
        }

        if (jwtUtil.isExpired(refreshToken)) {
            return JwtTokenRefreshDto.error("Refresh Token이 만료되었습니다.");
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("accessToken", username, role, 60 * 60 * 1000L);
        String newRefreshToken = jwtUtil.createJwt("refreshToken", username, role, 24 * 60 * 1000L);

        jwtTokenRepository.deleteByRefresh(refreshToken);


        response.setHeader("Authorization", "Bearer" + newAccessToken);
        response.addCookie(jwtUtil.createCookie("refreshToken", newRefreshToken));

        return JwtTokenRefreshDto.success(newAccessToken, newRefreshToken);
    }
}
