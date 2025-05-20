package com.moyo.backend.follow.application;

import com.moyo.backend.follow.dto.GithubFollowUser;
import com.moyo.backend.follow.dto.UserFollowCommandMeta;
import com.moyo.backend.follow.dto.UserFollowDetectMeta;

import java.util.List;

public interface GithubFollowClient {

    int follow(String username, String accessToken);

    int unfollow(String username, String accessToken);

    UserFollowDetectMeta getUserFollowDetectMeta(String accessToken, String username);

    UserFollowCommandMeta getUserFollowCommandMeta(String oauthAccessToken, String username);

    List<GithubFollowUser> getFollowingList(String accessToken, int curPage);

    List<GithubFollowUser> getFollowerList(String accessToken, int curPage);
}
