package com.moyo.backend.security.config;

import com.moyo.backend.security.jwt.filter.JWTFilter;
import com.moyo.backend.security.jwt.util.JwtPayloadReader;
import com.moyo.backend.security.jwt.util.JwtProvider;
import com.moyo.backend.security.jwt.util.JwtValidator;
import com.moyo.backend.security.oauth.handler.OAuthLoginSuccessHandler;
import com.moyo.backend.security.oauth.service.GithubOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

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
                .requestMatchers("/auth/reissue/token", "/health", "/").permitAll()
                .anyRequest().authenticated()
        );

        http
                .addFilterAfter(new JWTFilter(jwtValidator,jwtPayloadReader), OAuth2LoginAuthenticationFilter.class);

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
