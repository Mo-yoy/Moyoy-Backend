package com.moyoy.api.user.application.response;

import com.moyoy.domain.user.Role;
import com.moyoy.domain.user.User;

public record UserSyncResult(
	Long id,
	Integer githubUserId,
	String username,
	String userProfile,
	Role role) {
	public static UserSyncResult from(User user) {
		return new UserSyncResult(
			user.getId(),
			user.getGithubUserId(),
			user.getUsername(),
			user.getProfileImgUrl(),
			user.getRole());
	}
}
