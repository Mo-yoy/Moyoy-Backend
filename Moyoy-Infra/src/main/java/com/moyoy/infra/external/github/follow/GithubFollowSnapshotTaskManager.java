package com.moyoy.infra.external.github.follow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.moyoy.infra.database.redis.follow.GithubFollowSnapshotCacheManager;
import com.moyoy.infra.database.redis.support.RedissonLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowSnapshotTaskManager {

	private final GithubFollowSnapshotLoader githubFollowSnapshotLoader;

	@RedissonLock(key = "'follow:' + #userId", waitTime = 0L, leaseTime = 30L, timeUnit = TimeUnit.MINUTES)
	public CompletableFuture<Void> submit(Long userId, Integer githubUserId) {

		log.info("데이터 수집 요청 제출 | userId : {}, githubUserId : {}", userId, githubUserId);

		CompletableFuture<Void> result = githubFollowSnapshotLoader.load(userId, githubUserId);

		log.info("데이터 수집 요청 제출 성공 | userId : {}, githubUserId : {}", userId, githubUserId);

		return result;
	}

}
