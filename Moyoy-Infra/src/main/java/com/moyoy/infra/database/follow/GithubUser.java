package com.moyoy.infra.database.follow;

import com.moyoy.infra.external.github.user.GithubUserResponse;

public record GithubUser(
	Integer id,
	String username,
	String profileImgUrl) implements Comparable<GithubUser> {

	public static GithubUser from(GithubUserResponse githubUserResponse) {
		return new GithubUser(
			githubUserResponse.id(),
			githubUserResponse.login(),
			githubUserResponse.avatarUrl());
	}

	// id ASC
	@Override
	public int compareTo(GithubUser otherUser) {
		return this.id.compareTo(otherUser.id);
	}
}
