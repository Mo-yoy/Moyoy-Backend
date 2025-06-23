package com.moyo.backend.domain.github_follow.data_access;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.moyo.backend.domain.github_follow.implement.GithubUser;

public interface GithubFollowHttpClient {
	ResponseEntity<GithubUserFollowStats> fetchFollowStatsByUserId(Long userId, String accessToken);

	List<GithubUser> fetchPagedFollowers(int currentPage, String accessToken);

	List<GithubUser> fetchPagedFollowings(int currentPage, String accessToken);

	GithubUser fetchGithubFollowUserById(Long userId, String accessToken);

	int follow(String targetUsername, String accessToken);

	int unfollow(String targetUsername, String accessToken);
}
