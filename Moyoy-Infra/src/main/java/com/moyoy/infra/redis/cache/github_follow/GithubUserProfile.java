package com.moyoy.infra.redis.cache.github_follow;

import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

public record GithubUserProfile(
	Integer id,
	String username,
	String profileImgUrl) implements Comparable<GithubUserProfile> {

	@Override
	public int compareTo(GithubUserProfile otherUser) {
		return this.id.compareTo(otherUser.id);
	}

	public static GithubUserProfile of(Integer id, String username, String profileImgUrl) {
		return new GithubUserProfile(id, username, profileImgUrl);
	}

	public static GithubUserProfile from(GithubUserResponse githubUserResponse) {
		return new GithubUserProfile(
			githubUserResponse.id(),
			githubUserResponse.login(),
			githubUserResponse.avatarUrl());
	}
}
