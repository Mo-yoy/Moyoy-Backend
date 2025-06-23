package com.moyo.backend.domain.github_follow.data_access;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.moyo.backend.domain.github_follow.implement.GithubUser;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowRestClientImpl implements GithubFollowHttpClient {

	private final RestClient restClient;

	@Override
	public ResponseEntity<GithubUserFollowStats> fetchFollowStatsByUserId(Long userId, String accessToken) {
		return restClient.get()
			.uri("/user/{userId}", userId)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toEntity(GithubUserFollowStats.class);
	}

	@Override
	public List<GithubUser> fetchPagedFollowers(int currentPage, String accessToken) {

		return restClient.get()
			.uri("/user/followers?per_page=" + GITHUB_FOLLOW_QUERY_PAGING_SIZE + "&page=" + currentPage)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@Override
	public List<GithubUser> fetchPagedFollowings(int currentPage, String accessToken) {
		return restClient.get()
			.uri("/user/following?per_page=" + GITHUB_FOLLOW_QUERY_PAGING_SIZE + "&page=" + currentPage)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@Override
	public GithubUser fetchGithubFollowUserById(Long userId, String accessToken) {
		return restClient.get()
			.uri("/user/{userId}", userId)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toEntity(GithubUser.class).getBody();
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
