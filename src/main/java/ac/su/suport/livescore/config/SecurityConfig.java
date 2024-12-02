package ac.su.suport.livescore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 필요시 세션 생성
                        .maximumSessions(1) // 동시에 한 세션만 허용
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // SockJS의 iframe 허용
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 페이지 비활성화
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/api/users/login",
                                "/api/users/register",
                                "/api/users/forgot-password",
                                "/api/users/checkEmail",
                                "/api/users/checkNickname",
                                "/api/users/sendVerificationCode",
                                "/api/users/verifyCode",
                                "/",
                                "/start",
                                "/register",
                                "/nickname",
                                "/login",
                                "/chat/room",
                                "/chat/**",
                                "/LivePage/**",
                                "/api/users/**"
                        ).permitAll()
                        .requestMatchers("/api/chat/user").permitAll() // /api/chat/user에 대한 접근 허용
                        .requestMatchers("/api/chat/**").authenticated() // 나머지 /api/chat/** 경로는 인증 필요
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

