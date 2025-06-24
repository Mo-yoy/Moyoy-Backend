package com.moyo.backend.domain.temporary_batch.data_access;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.moyo.backend.domain.temporary_batch.GithubContributorDetails;
import com.moyo.backend.domain.temporary_batch.GithubRepoDetails;
import com.moyo.backend.domain.temporary_batch.RankingPreflight;

public interface GithubRankingHttpClient {

	ResponseEntity<RankingPreflight> fetchRankingPreflight(Long userId, String accessToken);

	List<GithubRepoDetails> fetchPagedRepos(int currentPage, String accessToken);

	List<GithubContributorDetails> fetchPagedContributors(int currentPage, String repoFullName, String accessToken);

	ResponseEntity<?> fetchContributorCommitActivity(String repoFullName, String accessToken);
}
