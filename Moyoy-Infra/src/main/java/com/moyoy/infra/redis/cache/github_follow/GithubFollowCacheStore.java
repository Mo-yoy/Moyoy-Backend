package com.moyoy.infra.redis.cache.github_follow;

import java.util.Collections;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowCacheStore {

	private static final String FOLLOW_CACHE_NAME = "followSnapshot";
	private static final String FOLLOW_CACHE_TTL = "900";

	private final CacheManager cacheManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final ObjectMapper objectMapper;

	public Optional<GithubFollowSnapshot> findFollowSnapshot(Long userId) {

		GithubFollowSnapshot githubFollowSnapshot = cacheManager.getCache(FOLLOW_CACHE_NAME).get(userId, GithubFollowSnapshot.class);
		return Optional.ofNullable(githubFollowSnapshot);
	}

	@CachePut(cacheNames = FOLLOW_CACHE_NAME, key = "#userId")
	public GithubFollowSnapshot save(Long userId, GithubFollowSnapshot githubFollowRelationSnapshot) {
		return githubFollowRelationSnapshot;
	}

	@CacheEvict(cacheNames = FOLLOW_CACHE_NAME, key = "#userId")
	public void delete(Long userId) {

	}

	public boolean saveWithVersionCheck(Long userId, GithubFollowSnapshot snapshot, Long expectedVersion) {

		try {
			String jsonString = objectMapper.writeValueAsString(snapshot);

			String script = "local current = redis.call('GET', KEYS[1]) " +
				"if current == false then " +
				"  return 1 " +
				"end " +
				"local currentVersion = cjson.decode(current).version " +
				"if currentVersion == tonumber(ARGV[2]) then " +
				"  redis.call('SET', KEYS[1], ARGV[1], 'EX', ARGV[3]) " +
				"  return 1 " +
				"else " +
				"  return 0 " +
				"end";

			Long result = stringRedisTemplate.execute(
				RedisScript.of(script, Long.class),
				Collections.singletonList(FOLLOW_CACHE_NAME + "::" + userId),
				jsonString,
				expectedVersion.toString(),
				FOLLOW_CACHE_TTL);

			log.info("{}의 캐시 보정 | 낙관락 결과 = {}", userId, result);
			return result == 1;

		} catch (JsonProcessingException e) {
			throw new RuntimeException("스냅샷 저장 실패", e);
		}
	}
}
