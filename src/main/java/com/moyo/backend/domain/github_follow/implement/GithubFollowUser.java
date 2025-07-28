package com.moyo.backend.domain.github_follow.implement;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubFollowUser(
	@JsonProperty("id") Integer id,
	@JsonProperty("login") String username,
	@JsonProperty("avatar_url") String profileImgUrl) implements Comparable<GithubFollowUser> {

	// id ASC
	@Override
	public int compareTo(GithubFollowUser otherUser) {
		return this.id.compareTo(otherUser.id);
	}
}
