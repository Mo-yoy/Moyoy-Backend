package com.moyoy.infra.external.github.follow;

import java.util.List;

import com.moyoy.domain.github_follow.GithubUser;

public interface GithubFollowClient {

	List<GithubUser> fetchFollowings(Long userId, Integer githubUserId);

	List<GithubUser> fetchFollowers(Long userId, Integer githubUserId);

	void follow(Long currentUserId, Integer targetUserGithubId);

	void unFollow(Long currentUserId, Integer targetUserGithubId);
}
