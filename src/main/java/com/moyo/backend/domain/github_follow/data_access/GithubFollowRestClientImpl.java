package com.moyo.backend.domain.github_follow.data_access;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.moyo.backend.domain.github_follow.implement.GithubFollowUser;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowRestClientImpl implements GithubFollowHttpClient {

	private final RestClient restClient;

	@Override
	public ResponseEntity<GithubUserFollowStats> fetchFollowStats(Integer githubUserId, String accessToken) {
		return restClient.get()
			.uri("/user/{userId}", githubUserId)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toEntity(GithubUserFollowStats.class);
	}

	@Override
	public List<GithubFollowUser> fetchPagedFollowers(int currentPage, String accessToken) {

		return restClient.get()
			.uri("/user/followers?per_page=" + GITHUB_FOLLOW_QUERY_PAGING_SIZE + "&page=" + currentPage)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@Override
	public List<GithubFollowUser> fetchPagedFollowings(int currentPage, String accessToken) {
		return restClient.get()
			.uri("/user/following?per_page=" + GITHUB_FOLLOW_QUERY_PAGING_SIZE + "&page=" + currentPage)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@Override
	public GithubFollowUser fetchGithubFollowUserById(Integer githubUserId, String accessToken) {
		return restClient.get()
			.uri("/user/{userId}", githubUserId)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toEntity(GithubFollowUser.class).getBody();
	}

	@Override
	public int follow(String targetUsername, String accessToken) {
		return restClient.put()
			.uri("/user/following/{username}", targetUsername)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toBodilessEntity()
			.getStatusCode().value();
	}

	@Override
	public int unfollow(String targetUsername, String accessToken) {
		return restClient.delete()
			.uri("/user/following/{username}", targetUsername)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toBodilessEntity()
			.getStatusCode().value();
	}
}
