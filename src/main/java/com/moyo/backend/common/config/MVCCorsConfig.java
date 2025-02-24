package com.moyo.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.moyo.backend.common.constant.MoyoConstants.AUTHORIZATION;

@Configuration
public class MVCCorsConfig implements WebMvcConfigurer {

    @Value("${spring.cors.allow-origin}")
    private String allowOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowOrigin)
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders(AUTHORIZATION)
                .allowCredentials(true);
    }
}
