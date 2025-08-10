package com.moyoy.api.auth.security.support;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl.*;

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
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import com.moyoy.api.auth.jwt.support.JwtAuthenticationFilter;
import com.moyoy.api.auth.jwt.support.JwtExceptionHandleFilter;
import com.moyoy.api.auth.security.component.CustomAccessDeniedHandler;
import com.moyoy.api.auth.security.component.CustomAuthenticationEntryPoint;
import com.moyoy.api.auth.security.component.CustomOAuth2UserService;
import com.moyoy.api.auth.security.component.HttpCookieOAuth2AuthorizationRequestRepository;
import com.moyoy.api.auth.security.component.OAuth2AuthenticationFailureHandler;
import com.moyoy.api.auth.security.component.OAuth2AuthenticationSuccessHandler;
import com.moyoy.api.auth.security.component.RdbOAuth2AuthorizedClientService;
import com.moyoy.api.common.filter.RequestInfoMDCFilter;
import com.moyoy.api.common.filter.UserContextMDCFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
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
	private final UserContextMDCFilter userContextMDCFilter;
	private final RequestInfoMDCFilter requestInfoMDCFilter;

	@Bean
	public SecurityFilterChain moyoySecurityFilterChain(HttpSecurity http) throws Exception {

		http
			.formLogin(AbstractHttpConfigurer::disable)
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterAfter(requestInfoMDCFilter, DisableEncodeUrlFilter.class)
			.addFilterBefore(jwtAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
			.addFilterBefore(jwtExceptionHandleFilter, JwtAuthenticationFilter.class)
			.addFilterAfter(userContextMDCFilter, AnonymousAuthenticationFilter.class)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/health", "/").permitAll() // Health Check
				.requestMatchers("/permit/all/test", "/test/**").permitAll() // Test
				.requestMatchers("/auth/test/admin").hasRole("ADMIN")
				.requestMatchers("/error/**", "/favicon.ico").permitAll() // Server Default
				.requestMatchers("/api/v1/auth/reissue/token").permitAll() // Token Reissue
				.requestMatchers("/swagger-ui.html", "/static/swagger-ui/**").permitAll() // Swagger UI
				.requestMatchers("/api/v1/rankings").permitAll() // [Domain] Ranking
				.requestMatchers(GET, "/api/v1/pr-review").permitAll()
				.requestMatchers("/api/v1/pr-review/{pr-reviewId}").permitAll()
				.anyRequest().authenticated())
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
					.baseUri("/auth/login")
					.authorizationRequestRepository(authorizationRequestRepository))
				.userInfoEndpoint(userInfo -> userInfo
					.userService(oAuth2UserService))
				.authorizedClientService(authorizedClientService)
				.successHandler(successHandler)
				.failureHandler(failureHandler))
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(authenticationEntryPoint)
				.accessDeniedHandler(accessDeniedHandler));

		return http.build();
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		return fromHierarchy("ROLE_ADMIN > ROLE_USER\n" +
			"ROLE_USER > ROLE_ANONYMOUS");
	}

}
