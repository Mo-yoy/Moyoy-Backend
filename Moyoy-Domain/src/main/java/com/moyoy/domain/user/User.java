package com.moyoy.domain.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {

	private Long id;
	private Integer githubUserId;
	private String username;
	private String profileImgUrl;
	private Role role;

	public static User from(UserCreate userCreate) {

		return User.builder()
			.githubUserId(userCreate.githubUserId())
			.username(userCreate.username())
			.profileImgUrl(userCreate.profileImgUrl())
			.role(Role.USER)
			.build();
	}

	public void changeProfile(String username, String profileImgUrl) {
		this.username = username;
		this.profileImgUrl = profileImgUrl;
	}
}
