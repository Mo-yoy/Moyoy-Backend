package com.moyoy.infra.redis.cache.github_follow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.redisson.api.RedissonClient;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubFollowCacheStore {

	private static final String FOLLOW_CACHE_NAME = "followSnapshot";

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

			String script =
					"local current = redis.call('GET', KEYS[1]) " +
							"if current == false then " +
							"  redis.call('SET', KEYS[1], ARGV[1]) " +
							"  return 1 " +
							"end " +
							"local currentVersion = cjson.decode(current).version " +
							"if currentVersion == tonumber(ARGV[2]) then " +
							"  redis.call('SET', KEYS[1], ARGV[1]) " +
							"  return 1 " +
							"else " +
							"  return 0 " +
							"end";

			Long result = stringRedisTemplate.execute(
					RedisScript.of(script, Long.class),
					Collections.singletonList("github:follow:" + userId),
					jsonString,
					expectedVersion.toString()
			);

			return result == 1;

		} catch (JsonProcessingException e) {
			throw new RuntimeException("스냅샷 저장 실패", e);
		}
	}
}
