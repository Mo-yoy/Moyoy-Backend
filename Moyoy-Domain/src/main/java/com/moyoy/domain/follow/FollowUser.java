package com.moyoy.domain.follow;

public record FollowUser(
	Integer id,
	String username,
	String profileImgUrl) implements Comparable<FollowUser> {

	// id ASC
	@Override
	public int compareTo(FollowUser otherUser) {
		return this.id.compareTo(otherUser.id);
	}
}
