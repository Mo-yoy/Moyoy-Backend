package com.moyo.backend.githubFollow.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubFollowUser(@JsonProperty("id") Long id,
	@JsonProperty("login") String username,
	@JsonProperty("avatar_url") String profileImgUrl) implements Comparable<GithubFollowUser> {

	// id ASC
	@Override
	public int compareTo(GithubFollowUser otherUser) {
		return this.id.compareTo(otherUser.id);
	}
}
