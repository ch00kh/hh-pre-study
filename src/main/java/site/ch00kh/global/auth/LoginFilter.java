package site.ch00kh.global.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.ch00kh.domain.account.dao.jwttoken.JwtToken;
import site.ch00kh.domain.account.dao.jwttoken.JwtTokenRepository;
import site.ch00kh.domain.account.dto.AccountDetails;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JWTUtil jwtUtil;
    private final JwtTokenRepository refreshRepository;

    // 검증 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        System.out.println("=== LOGIN FILTER ACTION ===");

        //클라이언트 요청에서 username, password 추출
        String username;
        String password;
        try {
            if (request.getContentType() != null && request.getContentType().contains("application/json")) {
                // JSON 데이터인 경우
                Map<String, String> jsonRequest = objectMapper.readValue(request.getInputStream(), Map.class);
                username = jsonRequest.get("username");
                password = jsonRequest.get("password");
            } else {
                // form-data인 경우
                username = obtainUsername(request);
                password = obtainPassword(request);
            }

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

            // id, pw, 권한을 담은 token을 AuthenticationManager(ProviderManager)로 전달
            // -> ProviderManager는 적절한 Provider(DaoAuthenticationProvider)를 찾아 token을 전달
            // -> DaoAuthenticationProvider가 UserDetailsService의 구현체인 CustomUserDetailsService를 사용
            // -> loadUserByUsername()을 통행 CustomUserDetails를 획득하여 사용자의 정보조회 및 검증
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse authentication request body", e);
        }
    }

    //로그인 성공시 실행하는 메소드 -> JWT 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //UserDetails
        AccountDetails customUserDetails = (AccountDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        String role = authentication.getAuthorities().stream()
                .iterator().next()
                .getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 60 * 60 * 10L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 60 * 60 * 24 * 1000L);

        //Refresh 토큰 저장
        addRefreshEntity(username, refresh, 86400000L);

        //응답 설정
        response.setHeader("Bearer ", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        JwtToken token = new JwtToken();
        token.setUsername(username);
        token.setRefresh(refresh);
        token.setExpiration(date.toString());

        refreshRepository.save(token);
    }
}
