package com.moyo.backend.ranking.batch;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_QUERY_PAGING_SIZE;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubRankingRestClientImpl implements GithubRankingHttpClient {

	private final RestClient restClient;

	@Override
	public ResponseEntity<RankingPreflight> fetchRankingPreflight(Long userId, String accessToken) {

		return restClient.get()
			.uri("/user/{userId}", userId)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toEntity(RankingPreflight.class);
	}

	@Override
	public List<GithubRepoDetails> fetchPagedRepos(int currentPage, String accessToken) {

		return restClient.get()
			.uri("/user/repos?affiliation=owner,organization_member&since=2025-01-01T00:00:00Z&per_page=" + GITHUB_QUERY_PAGING_SIZE + "&page=" + currentPage)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@Override
	public List<GithubContributorDetails> fetchPagedContributors(int currentPage, String repoFullName, String accessToken) {

		return restClient.get()
			.uri("/repos/" + repoFullName + "/contributors")
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@Override
	public ResponseEntity<?> fetchContributorCommitActivity(String repoFullName, String accessToken) {

		return restClient.get()
			.uri("/repos/" + repoFullName + "/stats/contributors")
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toEntity(String.class);

	}
}
