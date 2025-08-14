package com.moyoy.core.domain.user.implement;

import com.moyoy.infra.database.domain.user.UserEntity;

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
