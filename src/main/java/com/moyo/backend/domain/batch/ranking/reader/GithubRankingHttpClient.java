package com.moyo.backend.domain.batch.ranking.reader;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.moyo.backend.domain.batch.ranking.dto.GithubContributorDetailsResponse;
import com.moyo.backend.domain.batch.ranking.dto.GithubRepoDetailsResponse;
import com.moyo.backend.domain.batch.ranking.dto.GithubProfileForRanking;

public interface GithubRankingHttpClient {

	ResponseEntity<GithubProfileForRanking> fetchRankingPreflight(Integer githubUserId, String accessToken);

	List<GithubRepoDetailsResponse> fetchPagedRepos(int currentPage, String accessToken);

	List<GithubContributorDetailsResponse> fetchPagedContributors(int currentPage, String repoFullName, String accessToken);

	ResponseEntity<?> fetchContributorCommitActivity(String repoFullName, String accessToken);
}
