package com.moyo.backend.domain.batch.ranking.reader;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.moyo.backend.domain.batch.ranking.dto.GithubContributorDetails;
import com.moyo.backend.domain.batch.ranking.dto.GithubRepoDetails;
import com.moyo.backend.domain.batch.ranking.dto.RankingPreflight;

public interface GithubRankingHttpClient {

	ResponseEntity<RankingPreflight> fetchRankingPreflight(Long userId, String accessToken);

	List<GithubRepoDetails> fetchPagedRepos(int currentPage, String accessToken);

	List<GithubContributorDetails> fetchPagedContributors(int currentPage, String repoFullName, String accessToken);

	ResponseEntity<?> fetchContributorCommitActivity(String repoFullName, String accessToken);
}
