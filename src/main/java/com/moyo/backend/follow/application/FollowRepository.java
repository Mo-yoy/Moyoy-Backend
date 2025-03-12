package com.moyo.backend.follow.application;

import com.moyo.backend.follow.dto.GithubUserResponse;

import java.util.List;

public interface FollowRepository {

    int follow(String username, String accessToken);

    int unfollow(String username, String accessToken);

    List<GithubUserResponse> getFollowingList(String accessToken);

    List<GithubUserResponse> getFollowerList(String accessToken);
}
