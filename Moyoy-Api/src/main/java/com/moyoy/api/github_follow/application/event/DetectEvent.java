package com.moyoy.api.github_follow.application.event;

import com.moyoy.domain.user.User;

public record DetectEvent(
	Long userId,
	Integer githubUserId) {

	public static DetectEvent from(User user) {
		return new DetectEvent(user.getId(), user.getGithubUserId());
	}
}
