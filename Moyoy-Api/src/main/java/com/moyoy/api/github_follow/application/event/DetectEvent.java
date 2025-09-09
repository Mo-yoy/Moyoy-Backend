package com.moyoy.api.github_follow.application.event;

public record DetectEvent(
	Long userId,
	Integer githubUserId) {

}
