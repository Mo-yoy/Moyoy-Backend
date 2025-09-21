package com.moyoy.api.auth.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfig {

	@Value("${actuator.monitoring.password}")
	private String monitoringPassword;

	@Bean
	@Order(1)
	public SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {
		return http
			.securityMatcher("/actuator/**")
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/actuator/prometheus")
				.hasRole("MONITORING"))
			.httpBasic(Customizer.withDefaults())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.build();
	}

	@Bean
	public InMemoryUserDetailsManager userDetailsService() {

		UserDetails monitoring = User.builder()
			.username("prometheus")
			.password("{noop}" + monitoringPassword)
			.roles("MONITORING")
			.build();

		return new InMemoryUserDetailsManager(monitoring);
	}

}
