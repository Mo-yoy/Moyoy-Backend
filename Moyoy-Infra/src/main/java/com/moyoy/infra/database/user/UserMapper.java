package com.moyoy.infra.database.user;

import com.moyoy.domain.user.User;

public class UserMapper {

	public static User toModel(UserEntity userEntity) {

		return User.builder()
			.id(userEntity.getId())
			.githubUserId(userEntity.getGithubUserId())
			.username(userEntity.getUsername())
			.profileImgUrl(userEntity.getProfileImgUrl())
			.role(userEntity.getRole())
			.build();
	}

	public static UserEntity toEntity(User user) {

		return UserEntity.builder()
			.id(user.getId())
			.githubUserId(user.getGithubUserId())
			.username(user.getUsername())
			.profileImgUrl(user.getProfileImgUrl())
			.role(user.getRole())
			.build();
	}
}
