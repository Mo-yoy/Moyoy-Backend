package com.moyo.backend.security.config;

import com.moyo.backend.security.jwt.filter.JwtAuthenticationFilter;
import com.moyo.backend.security.jwt.filter.JwtExceptionHandleFilter;
import com.moyo.backend.security.oauth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl.fromHierarchy;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2AuthenticationFailureHandler failureHandler;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final RdbOAuth2AuthorizedClientService authorizedClientService;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionHandleFilter jwtExceptionHandleFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain moyoySecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterBefore(jwtExceptionHandleFilter, JwtAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/").permitAll()               // Health Check
                        .requestMatchers("/permit/all/test").permitAll()             // Test
                        .requestMatchers("/auth/only/test").hasRole("ADMIN")
                        .requestMatchers("/error/**","/favicon.ico" ).permitAll()  // Default
                        .requestMatchers("/auth/reissue/token").permitAll()          // Token Reissue
                        .anyRequest().authenticated())
                .oauth2Login(oauth2  -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/auth/login")
                                .authorizationRequestRepository(authorizationRequestRepository)
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        .authorizedClientService(authorizedClientService)
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .exceptionHandling(exception-> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return fromHierarchy("ROLE_ADMIN > ROLE_USER\n" +
                             "ROLE_USER > ROLE_ANONYMOUS");
    }

}