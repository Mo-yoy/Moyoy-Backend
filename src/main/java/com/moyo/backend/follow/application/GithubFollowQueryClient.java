package com.moyo.backend.follow.application;

import com.moyo.backend.follow.dto.GithubUserResponse;

import java.util.List;

public interface GithubFollowQueryClient {

    List<GithubUserResponse> getFollowingList(String accessToken);

    List<GithubUserResponse> getFollowerList(String accessToken);
}
