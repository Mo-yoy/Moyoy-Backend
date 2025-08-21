package com.moyoy.infra.external.github.follow;

import java.util.List;

public interface GithubFollowClient {

	List<?> fetchFollowings(Long userId, Integer githubUserId);

	List<?> fetchFollowers(Long userId, Integer githubUserId);

	void follow(Long currentUserId, Integer currentUserGithubId, Integer targetUserGithubId);

	void unFollow(Long currentUserId, Integer currentUserGithubId, Integer targetUserGithubId);
}
