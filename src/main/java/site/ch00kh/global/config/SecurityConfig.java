package site.ch00kh.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import site.ch00kh.domain.account.dao.jwttoken.JwtTokenRepository;
import site.ch00kh.global.auth.CustomLogoutFilter;
import site.ch00kh.global.auth.JWTFilter;
import site.ch00kh.global.auth.JWTUtil;
import site.ch00kh.global.auth.LoginFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final JwtTokenRepository jwtTokenRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, JwtTokenRepository jwtTokenRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.jwtTokenRepository = jwtTokenRepository;
    }

    // BCryptPasswordEncoder : 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager : 검증 인터페이스(Login Filter에서 동작)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)                 // csrf : disable
                .formLogin(AbstractHttpConfigurer::disable)        // From 로그인 방식 : disable
                .httpBasic(AbstractHttpConfigurer::disable)        // http basic 인증 방식 : disable
                .authorizeHttpRequests((auth) -> auth       // 경로별 인가 작업
                        .requestMatchers("/api/signup").permitAll()
                        .requestMatchers("/login", "/").permitAll()
                        .requestMatchers("/reissue").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())

                // CORS 설정
                .cors((corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 프론트엔드 서버
                    configuration.setAllowedMethods(Collections.singletonList("*"));                     // 모든 메소드 허용
                    configuration.setAllowCredentials(true);                                             // 프론트엔드에 설정하면 백엔드도 해야함
                    configuration.setAllowedHeaders(Collections.singletonList("*"));                     // 모든 헤더 허용
                    configuration.setMaxAge(3600L);                                                      // 허용 시간
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));         // 프론트엔드로 넘겨줄 Header 설정

                    return configuration;
                })))

                // JWTFilter 등록 (LoginFilter 이전에 동작)
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)

                // UsernamePasswordAuthenticationFilter 위치에 커스텀한 LoginFilter를 등록
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, jwtTokenRepository), UsernamePasswordAuthenticationFilter.class)

                // CustomLogoutFilter 등록 (LogoutFilter 이전에 동작)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, jwtTokenRepository), LogoutFilter.class)

                .sessionManagement((session) -> session     // 세션 설정
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT를 통한 인증/인가를 위해서 세션을 STATELESS 상태로 설정하는 것이 중요


        return http.build();
    }
}