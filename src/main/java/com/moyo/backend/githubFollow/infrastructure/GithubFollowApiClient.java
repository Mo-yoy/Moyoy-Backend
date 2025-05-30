package com.moyo.backend.githubFollow.infrastructure;

import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import com.moyo.backend.githubFollow.infrastructure.GithubFollowDetectInfo;

import java.util.List;

public interface GithubFollowApiClient {
    GithubFollowUser getFollowUserInfo(Long userId, String accessToken);

    GithubFollowDetectInfo fetchFollowDetectInfo(String username, String accessToken);

    List<GithubFollowUser> getFollowingList(int curPage, String accessToken);

    List<GithubFollowUser> getFollowerList(int curPage, String accessToken);

    int follow(String targetUsername, String accessToken);

    int unfollow(String targetUsername, String accessToken);
}
