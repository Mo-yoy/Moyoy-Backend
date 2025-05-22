package com.moyo.backend.follow.application;

import com.moyo.backend.follow.domain.GithubFollowDetectInfo;
import com.moyo.backend.follow.dto.response.GithubFollowUserInfoResponse;

import java.util.List;

public interface GithubFollowApiClient {

    int follow(String username, String accessToken);

    int unfollow(String username, String accessToken);

    GithubFollowUserInfoResponse getFollowUserInfo(Long userId, String accessToken);

    List<GithubFollowUserInfoResponse> getFollowingList(String accessToken, int curPage);

    List<GithubFollowUserInfoResponse> getFollowerList(String accessToken, int curPage);

    GithubFollowDetectInfo fetchFollowDetectInfo(String currentUsername, String oauthAccessToken);


}
