package com.moyo.backend.ranking.batch;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GithubRankingHttpClient {

    ResponseEntity<RankingPreflight> fetchRankingPreflight(Long userId, String accessToken);

    List<GithubRepoDetails> fetchPagedRepos(int currentPage, String accessToken);

    List<GithubContributorDetails> fetchPagedContributors(int currentPage,String repoFullName, String accessToken);

    ResponseEntity<?> fetchContributorCommitActivity(String repoFullName, String accessToken);
}
