package com.moyo.backend.domain.batch.ranking.reader;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_QUERY_PAGING_SIZE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.moyo.backend.domain.batch.ranking.dto.GithubContributorDetailsResponse;
import com.moyo.backend.domain.batch.ranking.dto.GithubRepoDetailsResponse;
import com.moyo.backend.domain.batch.ranking.dto.GithubProfileForRanking;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubRankingRestClientImpl implements GithubRankingHttpClient {

	private final RestClient restClient;

	@Override
	public ResponseEntity<GithubProfileForRanking> fetchRankingPreflight(Integer githubUserId, String accessToken) {

		return restClient.get()
			.uri("/user/{userId}", githubUserId)
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toEntity(GithubProfileForRanking.class);
	}

	@Override
	public List<GithubRepoDetailsResponse> fetchPagedRepos(int currentPage, String accessToken) {

		String currentYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
		String since = currentYear + "-01-01T00:00:00Z";

		return restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/user/repos")
				.queryParam("affiliation", "owner,organization_member")
				.queryParam("since", since)
				.queryParam("per_page", GITHUB_QUERY_PAGING_SIZE)
				.queryParam("page", currentPage)
				.build())
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@Override
	public List<GithubContributorDetailsResponse> fetchPagedContributors(int currentPage, String repoFullName, String accessToken) {

		return restClient.get()
			.uri("/repos/" + repoFullName + "/contributors")
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@Override
	public ResponseEntity<String> fetchContributorCommitActivity(String repoFullName, String accessToken) {

		return restClient.get()
			.uri("/repos/" + repoFullName + "/stats/contributors")
			.headers(header -> header.setBearerAuth(accessToken))
			.retrieve()
			.toEntity(String.class);
	}
}
