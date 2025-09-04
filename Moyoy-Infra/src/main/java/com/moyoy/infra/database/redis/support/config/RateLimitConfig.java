package com.moyoy.infra.database.redis.support.config;

import static java.time.Duration.*;

import java.time.Duration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.redisson.cas.RedissonBasedProxyManager;

@Configuration
public class RateLimitConfig {

	/** Redis에 저장될 버킷 상태의 만료 전략
	 *  - 버킷이 다시 가득 찰 때까지의 시간 기준으로 TTL 설정 (안 쓰는 키 자동 정리)
	 */
	private static final ExpirationAfterWriteStrategy EXPIRATION =
		ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(ofSeconds(10));

	@Bean
	public BucketConfiguration defaultBucketConfiguration() {
		return BucketConfiguration.builder()
			.addLimit(limit ->
				limit.capacity(3)
					.refillIntervally(3, Duration.ofMinutes(1))
					.id("per-minute")
			)
			.build();
	}


	/** Redis 분산 버킷 매니저 (Redisson) */
	@Bean
	public ProxyManager<String> redissonProxyManager(RedissonClient redissonClient) {

		// Bucket4j가 Redisson의 내부 CommandExecutor를 요구함
		CommandAsyncExecutor commandExecutor = ((Redisson) redissonClient).getCommandExecutor();

		ClientSideConfig config = ClientSideConfig.getDefault()
			.withExpirationAfterWriteStrategy(EXPIRATION);

		return RedissonBasedProxyManager.builderFor(commandExecutor)
			.withClientSideConfig(config)
			.build();
	}
}
