package com.moyoy.domain.follow;

public interface FollowRelationRepository {

	FollowRelation loadFollowRelation(Long userId, boolean forceSync, Integer githubUserId, String accessToken);

	void follow(Long currentUserId, Integer currentUserGithubId, Integer targetUserGithubId, String accessToken);

	void unfollow(Long currentUserId, Integer currentUserGithubId, Integer targetUserGithubId, String accessToken);
}
