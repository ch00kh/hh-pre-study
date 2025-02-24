package site.ch00kh.global.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.ch00kh.domain.account.dao.jwttoken.JwtToken;
import site.ch00kh.domain.account.dao.jwttoken.JwtTokenRepository;
import site.ch00kh.domain.account.dto.AccountDetails;
import site.ch00kh.global.common.ResponseCode;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static site.ch00kh.global.common.ResponseCode.*;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final JwtTokenRepository refreshRepository;

    public CustomLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, JwtTokenRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username;
        String password;
        try {
            if (request.getContentType() != null && request.getContentType().contains("application/json")) {
                Map<String, String> jsonRequest = objectMapper.readValue(request.getInputStream(), Map.class);
                username = jsonRequest.get("username");
                password = jsonRequest.get("password");

            } else {
                username = obtainUsername(request);
                password = obtainPassword(request);
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse authentication request body", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        //UserDetails
        AccountDetails customUserDetails = (AccountDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        String role = authentication.getAuthorities().stream()
                .iterator().next()
                .getAuthority();

        //토큰 생성
        String accessToken = jwtUtil.createJwt("accessToken", username, role, 60 * 60 * 10L);
        String refreshToken = jwtUtil.createJwt("refreshToken", username, role, 60 * 60 * 24 * 1000L);

        //Refresh 토큰 저장
        jwtUtil.addRefreshEntity(username, refreshToken, 86400000L);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(jwtUtil.createCookie("refreshToken", refreshToken));

        FilterResponse.success(response, "로그인되었습니다.");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        FilterResponse.error(response, HttpStatus.UNAUTHORIZED, INVALID,"입력값을 확인해주세요.");
    }
}
