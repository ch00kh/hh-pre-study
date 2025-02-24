package site.ch00kh.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
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
import site.ch00kh.global.auth.*;
import site.ch00kh.global.common.ApiResponse;
import site.ch00kh.global.common.ResponseCode;
import site.ch00kh.global.common.Role;

import java.util.Collections;

import static site.ch00kh.global.common.ResponseCode.*;

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
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/signup").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/reissue").permitAll()
                        .requestMatchers("/admin").hasRole(Role.ADMIN.name())
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

                .addFilterBefore(new JWTFilter(jwtUtil), CustomLoginFilter.class)
                .addFilterAt(new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, jwtTokenRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, jwtTokenRepository), LogoutFilter.class)

                .sessionManagement((session) -> session     // 세션 설정
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT를 통한 인증/인가를 위해서 세션을 STATELESS 상태로 설정하는 것이 중요


        return http.build();
    }
}