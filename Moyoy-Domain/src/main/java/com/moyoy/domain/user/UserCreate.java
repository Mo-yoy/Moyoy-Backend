package com.moyoy.domain.user;

public record UserCreate(
	Integer githubUserId,
	String username,
	String profileImgUrl) {

}
