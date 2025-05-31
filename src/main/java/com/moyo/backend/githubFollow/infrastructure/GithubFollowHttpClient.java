package com.moyo.backend.githubFollow.infrastructure;

import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GithubFollowHttpClient {
    ResponseEntity<GithubUserFollowStats> fetchFollowStatsByUserId(Long userId, String accessToken);

    List<GithubFollowUser> fetchPagedFollowers(int currentPage, String accessToken);

    List<GithubFollowUser> fetchPagedFollowings(int currentPage, String accessToken);

    GithubFollowUser fetchGithubFollowUserById(Long userId, String accessToken);

    int follow(String targetUsername, String accessToken);

    int unfollow(String targetUsername, String accessToken);
}
