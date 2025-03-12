package com.moyo.backend.follow.application;

public interface GithubFollowCommandClient {

    int follow(String username, String accessToken);

    int unfollow(String username, String accessToken);
}
