package com.moyoy.infra.redis.support.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;

@Configuration
public class RedisConfig {

	private final String host;
	private final int port;

	public RedisConfig(
		@Value("${spring.data.redis.host}") String host,
		@Value("${spring.data.redis.port}") int port) {

		this.host = host;
		this.port = port;
	}

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://" + host + ":" + port);

		return Redisson.create(config);
	}

	// 메트릭 수동 등록, PRO 버전에서만 제공하고, 일부만 수동으로 볼수 있음
	@Bean
	public MeterBinder redissonMetrics(RedissonClient redissonClient) {
		return (registry) -> {
			Config config = ((RedissonClient)redissonClient).getConfig();

			// 커넥션 풀 사이즈 메트릭
			Gauge.builder("redisson.connection.pool.size", config,
				c -> c.useSingleServer() != null ? c.useSingleServer().getConnectionPoolSize() : 0)
				.description("Redisson connection pool size")
				.register(registry);

			// Pub/Sub 커넥션 풀 사이즈
			Gauge.builder("redisson.pubsub.connection.pool.size", config,
				c -> c.useSingleServer() != null ? c.useSingleServer().getSubscriptionConnectionPoolSize() : 0)
				.description("Redisson pub/sub connection pool size")
				.register(registry);
		};
	}
}
