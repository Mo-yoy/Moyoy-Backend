package com.moyoy.domain.common.config;

import java.time.Duration;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.moyoy.domain.follow.GithubFollowRelation;
import com.moyoy.infra.cache.CustomCacheErrorHandler;

/**
 *   [추후 확장시 고려 사항]
 *
 *   팔로우 뿐만 아니라 다른 객체도 범용적으로 저장 하는 캐시로 사용하고 싶다면 ObjectMapper의 activateDefaultTyping()을 통한 추가 설정이 필요함.
 *   이 경우, JSON 데이터에 클래스 메타 데이터가 추가로 저장됨.
 *
 *   또는, 각기 다른 직렬화기를 갖는 여러가지의 RedisCacheManager를 스프링 빈으로 등록하고 사용해도 됨.
 *   이 경우, 어떤 캐시 매니저를 사용할지 명시적으로 지정 해줘야 할 수 있음.
 */

@EnableCaching
@Configuration
public class CacheConfig implements CachingConfigurer {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Jackson2JsonRedisSerializer<?> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, GithubFollowRelation.class);

		RedisCacheConfiguration redisCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
			.disableCachingNullValues()
			.entryTtl(Duration.ofMinutes(15));

		return RedisCacheManager.builder(connectionFactory)
			.cacheDefaults(redisCacheConfig)
			.build();
	}

	@Override
	public CacheErrorHandler errorHandler() {
		return new CustomCacheErrorHandler();
	}
}
