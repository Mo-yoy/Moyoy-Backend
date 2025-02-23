package com.moyo.backend.security.config;

import com.moyo.backend.security.oauth.handler.OAuthLoginSuccessHandler;
import com.moyo.backend.security.oauth.service.GithubOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class OAuth2LoginSecurityConfig {

    private final GithubOAuth2UserService userService;
    private final OAuthLoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/reissue/token", "/health", "/").permitAll()
                .anyRequest().authenticated()
        );

        http
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth-> auth
                                .baseUri("/auth/login")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(userService)
                        )
                        .successHandler(loginSuccessHandler)
//                        .failureHandler()
                );

        return http.build();
    }
}
