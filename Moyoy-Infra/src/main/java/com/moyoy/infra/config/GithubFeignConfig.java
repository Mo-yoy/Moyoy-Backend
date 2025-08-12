package com.moyoy.infra.config;

import static com.moyoy.common.constant.MoyoConstants.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class GithubFeignConfig {

	@Bean
	public RequestInterceptor githubRequestInterceptor() {

		return requestTemplate -> {

			requestTemplate.header(CONTENT_TYPE, JSON);
			requestTemplate.header(ACCEPT, "application/vnd.github+json");
			requestTemplate.header("X-GitHub-Api-Version", "2022-11-28");
		};
	}
}
