package com.moyoy.domain.common.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	private final RedisProperties redisProperties;

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {

		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisProperties.host, redisProperties.port);

		LettuceClientConfiguration timeoutConfig = LettuceClientConfiguration.builder()
			.commandTimeout(Duration.ofSeconds(3))
			.build();

		return new LettuceConnectionFactory(configuration, timeoutConfig);
	}

	@RequiredArgsConstructor
	@ConfigurationProperties("spring.data.redis")
	static class RedisProperties {

		private final String host;
		private final int port;
	}
}
