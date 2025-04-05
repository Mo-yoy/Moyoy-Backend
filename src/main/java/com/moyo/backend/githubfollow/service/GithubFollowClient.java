package com.moyo.backend.githubfollow.service;

import com.moyo.backend.githubfollow.dto.UserFollowCommandMeta;
import com.moyo.backend.githubfollow.dto.UserFollowDetectMeta;
import com.moyo.backend.githubfollow.model.FollowUser;

import java.util.List;

public interface GithubFollowClient {

    int follow(String username, String accessToken);

    int unfollow(String username, String accessToken);

    UserFollowDetectMeta getUserFollowDetectMeta(String accessToken, String username);

    UserFollowCommandMeta getUserFollowCommandMeta(String oauthAccessToken, String username);

    List<FollowUser> getFollowingList(String accessToken, int curPage);

    List<FollowUser> getFollowerList(String accessToken, int curPage);

}
