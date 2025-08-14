package com.moyoy.core.domain.user.implement;

import com.moyoy.infra.database.domain.user.Role;
import com.moyoy.infra.database.support.entity.BaseTimeEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User extends BaseTimeEntity {

	private Long id;
	private Integer githubUserId;
	private String username;
	private String profileImgUrl;
	private Role role;

	public static User from(GithubUserProfileDto githubUserProfileDto) {
		return User.builder()
			.githubUserId(githubUserProfileDto.githubUserId())
			.username(githubUserProfileDto.username())
			.profileImgUrl(githubUserProfileDto.profileImgUrl())
			.build();
	}

	public void changeUsername(String username) {
		this.username = username;
	}

	public void changeProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}

	public void initRole() {
		this.role = Role.USER;
	}
}
