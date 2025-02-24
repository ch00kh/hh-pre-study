package site.ch00kh.global.auth;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.ch00kh.domain.account.dao.jwttoken.JwtToken;
import site.ch00kh.domain.account.dao.jwttoken.JwtTokenRepository;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;
    private final JwtTokenRepository jwtTokenRepository;

    // application.properties or application.yml 의 jwt 키 가져오기 (key는 String이 아닌 SecretKey로 객체로 생성)
    public JWTUtil(@Value("${spring.jwt.secret}")String secret, JwtTokenRepository jwtTokenRepository) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.jwtTokenRepository = jwtTokenRepository;
    }

    // 검증 메소드 : 검증에 필요한 LOGIN-ID 가져오기
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // 검증 메소드 : 검증에 필요한 role 가져오기
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    // 검증 메소드 : 만료 여부
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // 토큰 판단용 메소드
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // 토큰 생성 메소드
    public String createJwt(String category, String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)                                // LOGIN-ID
                .claim("role", role)                                        // ROLE
                .issuedAt(new Date(System.currentTimeMillis()))                 // 발행일자
                .expiration(new Date(System.currentTimeMillis() + expiredMs))   // 만료일자
                .signWith(secretKey)                                            // 토큰을 통해 시그니처 생성 및 암호화
                .compact();
    }

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    public void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        JwtToken token = new JwtToken();
        token.setUsername(username);
        token.setRefresh(refresh);
        token.setExpiration(date.toString());

        jwtTokenRepository.save(token);
    }
}
