package com.moyoy.api.common.config;

import static com.moyoy.common.constant.MoyoConstants.AUTHORIZATION;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcCorsConfig implements WebMvcConfigurer {

	@Value("${spring.cors.allow-origin}")
	private String allowOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(allowOrigins.split(","))
			.allowedMethods("*")
			.allowedHeaders("*")
			.exposedHeaders(AUTHORIZATION)
			.allowCredentials(true);
	}
}
