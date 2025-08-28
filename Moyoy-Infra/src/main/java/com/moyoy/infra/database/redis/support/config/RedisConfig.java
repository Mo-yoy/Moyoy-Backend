package com.moyoy.infra.database.redis.support.config;

import lombok.RequiredArgsConstructor;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	private final RedisProperties redisProperties;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://" + redisProperties.host + ":" + redisProperties.port);
		return Redisson.create(config);
	}

	@RequiredArgsConstructor
	@ConfigurationProperties("spring.data.redis")
	static class RedisProperties {

		private final String host;
		private final int port;
	}
}
