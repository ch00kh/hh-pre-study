package site.ch00kh.global.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import site.ch00kh.domain.account.dao.jwttoken.JwtTokenRepository;

import java.io.IOException;
import java.util.Arrays;

import static site.ch00kh.global.common.ResponseCode.BAD_REQUEST;
import static site.ch00kh.global.common.ResponseCode.INVALID;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (!request.getRequestURI().equals("/api/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!request.getMethod().equals("POST")) {
            FilterResponse.error(response, HttpStatus.BAD_REQUEST, BAD_REQUEST, "잘못된 HTTP Method 입니다.");
            return;
        }

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
            filterChain.doFilter(request, response);
            FilterResponse.error(response, HttpStatus.BAD_REQUEST, INVALID, "리프레시 토큰이 없습니다.");
            return;
        }

        if (jwtUtil.isExpired(refreshToken)) {
            FilterResponse.error(response, HttpStatus.BAD_REQUEST, INVALID, "만료된 토큰입니다.");
            return;
        }

        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refreshToken")) {
            FilterResponse.error(response, HttpStatus.BAD_REQUEST, INVALID, "유효하지 않는 토큰입니다.");
            return;
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = jwtTokenRepository.existsByRefresh(refreshToken);
        if (!isExist) {
            FilterResponse.error(response, HttpStatus.BAD_REQUEST, INVALID, "존재하지 않는 토큰입니다.");
            return;
        }

        jwtTokenRepository.deleteByRefresh(refreshToken);

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/api");
        response.addCookie(cookie);

        FilterResponse.success(response, "로그아웃 되었습니다.");
    }
}