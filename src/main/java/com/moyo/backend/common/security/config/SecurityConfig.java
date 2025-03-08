package com.moyo.backend.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyo.backend.common.security.jwt.filter.JWTAuthenticationExceptionHandleFilter;
import com.moyo.backend.common.security.jwt.filter.JWTAuthenticationFilter;
import com.moyo.backend.common.security.jwt.util.JwtPayloadReader;
import com.moyo.backend.common.security.jwt.util.JwtValidator;
import com.moyo.backend.common.security.oauth.handler.OAuthLoginSuccessHandler;
import com.moyo.backend.common.security.oauth.service.GithubOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final GithubOAuth2UserService userService;
    private final OAuthLoginSuccessHandler loginSuccessHandler;
    private final JwtPayloadReader jwtPayloadReader;
    private final JwtValidator jwtValidator;

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
                .requestMatchers("/auth/reissue/token", "/health", "/","/permit/all/test", "/error/**").permitAll()
                .anyRequest().authenticated()
        );

        http
                .addFilterAfter(new JWTAuthenticationFilter(jwtValidator,jwtPayloadReader), OAuth2LoginAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthenticationExceptionHandleFilter(objectMapper), JWTAuthenticationFilter.class);

        http
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth-> auth
                                .baseUri("/auth/login")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(userService)
                        )
                        .successHandler(loginSuccessHandler)
                );

        return http.build();
    }
}