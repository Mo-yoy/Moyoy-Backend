package com.moyoy.infra.config;

import static com.moyoy.common.constant.MoyoConstants.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class DiscordFeignConfig {

	@Bean
	public RequestInterceptor discordRequestInterceptor() {

		return requestTemplate -> {

			requestTemplate.header(CONTENT_TYPE, JSON);
		};
	}
}
