package com.moyoy.infra.external.github.repo;

import java.util.List;

public interface GithubRepoClient {

	List<GithubRepoResponse> fetchReposCreatedThisYear(String accessToken);

	List<GithubRepoContributorsResponse> fetchRepoContributors(String accessToken, String repoFullName);

	List<GithubRepoCommitStatsResponse> fetchRepoContributorStats(String repoFullName, String accessToken);
}
