package com.moyo.backend.githubFollow.application;

import com.moyo.backend.githubFollow.infrastructure.GithubFollowDetectInfo;
import com.moyo.backend.githubFollow.infrastructure.dto.GithubFollowUserInfoResponse;

import java.util.List;

public interface GithubFollowApiClientLegacy {

    int follow(String username, String accessToken);

    int unfollow(String username, String accessToken);

    GithubFollowUserInfoResponse getFollowUserInfo(Long userId, String accessToken);

    List<GithubFollowUserInfoResponse> getFollowingList(String accessToken, int curPage);

    List<GithubFollowUserInfoResponse> getFollowerList(String accessToken, int curPage);

    GithubFollowDetectInfo fetchFollowDetectInfo(String currentUsername, String oauthAccessToken);

}