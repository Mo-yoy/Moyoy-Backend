package com.moyo.backend.domain.batch.ranking.data_access;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface GithubRankingHttpClient {

	ResponseEntity<GithubProfileForRanking> fetchRankingPreflight(Integer githubUserId, String accessToken);

	List<GithubRepoDetailsResponse> fetchPagedRepos(int currentPage, String accessToken);

	List<GithubContributorDetailsResponse> fetchPagedContributors(int currentPage, String repoFullName, String accessToken);

	ResponseEntity<String> fetchContributorCommitActivity(String repoFullName, String accessToken);
}
