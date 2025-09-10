package com.moyoy.domain.user;

import lombok.Builder;
import lombok.Getter;

import com.moyoy.domain.user.dto.UserCreate;

@Getter
@Builder
public class User {

	private Long id;
	private Integer githubUserId;
	private String username;
	private String profileImgUrl;
	private SocialSize socialSize;
	private Role role;

	public static User from(UserCreate userCreate) {

		return User.builder()
			.githubUserId(userCreate.githubUserId())
			.username(userCreate.username())
			.profileImgUrl(userCreate.profileImgUrl())
			.socialSize(userCreate.socialSize())
			.role(Role.USER)
			.build();
	}

	public void changeProfile(String username, String profileImgUrl) {
		this.username = username;
		this.profileImgUrl = profileImgUrl;
	}

	public void changeSocialSize(SocialSize socialSize) {
		this.socialSize = socialSize;
	}
}
