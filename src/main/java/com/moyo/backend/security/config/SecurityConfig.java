package com.moyo.backend.security.config;

import com.moyo.backend.security.oauth.CustomOAuth2UserService;
import com.moyo.backend.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.moyo.backend.security.oauth.OAuth2AuthenticationFailureHandler;
import com.moyo.backend.security.oauth.RdbOAuth2AuthorizedClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final RdbOAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2AuthenticationFailureHandler failureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;


    @Bean
    public SecurityFilterChain moyoySecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .csrf(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/reissue/token", "/health", "/","/permit/all/test", "/error/**","/favicon.ico").permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(oauth2  -> oauth2
                        .loginPage("/login")
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/auth/login")
                                .authorizationRequestRepository(authorizationRequestRepository)
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        .authorizedClientService(authorizedClientService)
                        .failureHandler(failureHandler)
                );

        return http.build();
    }

}