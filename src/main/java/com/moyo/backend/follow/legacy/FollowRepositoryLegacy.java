package com.moyo.backend.follow.legacy;

import com.moyo.backend.follow.infrastructure.httpClient.dto.GithubUserResponse;

import java.util.List;

public interface FollowRepositoryLegacy {

    int follow(String username, String accessToken);

    int unfollow(String username, String accessToken);

    List<GithubUserResponse> getFollowingList(String accessToken);

    List<GithubUserResponse> getFollowerList(String accessToken);



}
