package com.moyo.backend.githubFollow.infrastructure;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.moyo.backend.githubFollow.domain.GithubFollowUser;

public interface GithubFollowHttpClient {
	ResponseEntity<GithubUserFollowStats> fetchFollowStatsByUserId(Long userId, String accessToken);

	List<GithubFollowUser> fetchPagedFollowers(int currentPage, String accessToken);

	List<GithubFollowUser> fetchPagedFollowings(int currentPage, String accessToken);

	GithubFollowUser fetchGithubFollowUserById(Long userId, String accessToken);

	int follow(String targetUsername, String accessToken);

	int unfollow(String targetUsername, String accessToken);
}
