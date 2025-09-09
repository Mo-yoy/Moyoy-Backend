package com.moyoy.infra.external.github.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.moyoy.domain.github_follow.GithubUser;

import com.moyoy.infra.external.github.user.GithubUserResponse;

@Component
@RequiredArgsConstructor
public class GithubFollowSnapshotPageReader {

	private final GithubFollowFeignClient githubFollowFeignClient;

	@Async
	public CompletableFuture<List<GithubUser>> readPage(String accessToken, int page) {

		List<GithubUserResponse> followingsResponseList = githubFollowFeignClient.fetchPagedFollowings(accessToken, GITHUB_MAX_QUERY_PAGING_SIZE, page);

		List<GithubUser> githubUserList = followingsResponseList.stream()
			.map(r -> new GithubUser(r.id(), r.login(), r.avatarUrl()))
			.toList();

		return CompletableFuture.completedFuture(githubUserList);
	}
}
