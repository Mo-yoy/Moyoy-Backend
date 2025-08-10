package com.moyoy.infra.cache;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 *   레디스 인스턴스 장애시 에러 임시 처리, 추후 구현할 예정
 */

@Slf4j
public class CustomCacheErrorHandler implements CacheErrorHandler {

	@Override
	public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
		handleExceptionInternal(exception);
	}

	@Override
	public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
		handleExceptionInternal(exception);
	}

	@Override
	public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
		handleExceptionInternal(exception);
	}

	@Override
	public void handleCacheClearError(RuntimeException exception, Cache cache) {
		handleExceptionInternal(exception);
	}

	private void handleExceptionInternal(RuntimeException exception) {
		log.error("Redis 서버 에러", exception);
	}
}
