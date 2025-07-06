package com.moyo.backend.domain.github_follow.implement;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubUser(
	@JsonProperty("id") Long id,
	@JsonProperty("login") String username,
	@JsonProperty("avatar_url") String profileImgUrl) implements Comparable<GithubUser> {

	// id ASC
	@Override
	public int compareTo(GithubUser otherUser) {
		return this.id.compareTo(otherUser.id);
	}
}
	