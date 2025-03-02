package site.ch00kh.global.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import site.ch00kh.domain.account.dao.Account;
import site.ch00kh.domain.account.dto.AccountDetails;
import site.ch00kh.global.common.ResponseCode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT 필터 실행 - URI: {}, Method: {}", request.getRequestURI(), request.getMethod());

        String authorization = request.getHeader("Authorization");
        String accessToken = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            accessToken = authorization.split(" ")[1];
        }

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            boolean expired = jwtUtil.isExpired(accessToken);
            log.debug("토큰 만료 여부: {}, 현재 시간: {}", expired, new Date());

        } catch (ExpiredJwtException e) {
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");
            response.setStatus(UNAUTHORIZED.value());
            log.error("토큰 만료 예외 발생! 토큰: {}", accessToken.substring(0, 10) + "...");
            return;
        }

        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("accessToken")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(UNAUTHORIZED.value());
            return;
        }

        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);
        Account account = new Account(username, role);

        AccountDetails accountDetails = new AccountDetails(account);

        Authentication authToken = new UsernamePasswordAuthenticationToken(accountDetails, null, accountDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
