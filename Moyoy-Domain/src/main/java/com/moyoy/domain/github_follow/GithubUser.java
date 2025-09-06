package com.moyoy.domain.github_follow;

public record GithubUser(
	Integer id,
	String username,
	String profileImgUrl) implements Comparable<GithubUser> {

	// id ASC
	@Override
	public int compareTo(GithubUser otherUser) {
		return this.id.compareTo(otherUser.id);
	}

	public static GithubUser of(Integer id, String username, String profileImgUrl) {
		return new GithubUser(id, username, profileImgUrl);
	}
}
