package com.moyoy.domain.follow;

public record GithubUser(
	Integer id,
	String username,
	String profileImgUrl) implements Comparable<GithubUser> {

	// id ASC
	@Override
	public int compareTo(GithubUser otherUser) {
		return this.id.compareTo(otherUser.id);
	}
}
