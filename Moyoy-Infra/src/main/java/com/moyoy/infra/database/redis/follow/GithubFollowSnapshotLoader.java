package com.moyoy.infra.database.redis.follow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.moyoy.domain.github_follow.GithubFollowSnapshot;
import com.moyoy.domain.github_follow.GithubUser;
import com.moyoy.domain.user.error.UserGithubTokenNotFoundException;

import com.moyoy.infra.database.mysql.query.port.GithubTokenReader;
import com.moyoy.infra.external.github.follow.GithubFollowClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowSnapshotLoader {

	private final GithubTokenReader githubTokenReader;
	private final GithubFollowClient githubFollowClient;
	private final GithubFollowSnapshotCacheManager githubFollowCacheManager;

	@Async("followLoadExecutor")
	public CompletableFuture<Void> load(Long userId, Integer githubUserId) {

		String bearerToken = githubTokenReader.findAccessTokenWithTokenType(userId).orElseThrow(UserGithubTokenNotFoundException::new);

		log.info("GithubFollowSnapshot 데이터 수집 시작 | userId : {}, githubUserId : {}", userId, githubUserId);

		List<GithubUser> githubFollowings = githubFollowClient.fetchFollowings(bearerToken, userId, githubUserId);
		List<GithubUser> githubFollowers = githubFollowClient.fetchFollowers(bearerToken, userId, githubUserId);

		GithubFollowSnapshot githubFollowRelationSnapshot = GithubFollowSnapshot.of(githubFollowers, githubFollowings, LocalDateTime.now());
		githubFollowCacheManager.save(userId, githubFollowRelationSnapshot);

		log.info("GithubFollowSnapshot 데이터 수집 완료 | userId : {}, githubUserId : {}", userId, githubUserId);

		return CompletableFuture.completedFuture(null);
	}

}
