package com.moyoy.domain.follow;

import com.moyoy.infra.github.dto.GithubFollowUserResponse;
import com.moyoy.infra.github.dto.GithubProfileResponse;

public record GithubFollowUser(
	Integer id,
	String username,
	String profileImgUrl
) implements Comparable<GithubFollowUser> {


	public static GithubFollowUser from(GithubProfileResponse githubProfileResponse){
		return new GithubFollowUser(
			githubProfileResponse.id(),
			githubProfileResponse.login(),
			githubProfileResponse.avatarUrl()
		);
	}

	public static GithubFollowUser from(GithubFollowUserResponse githubFollowUserResponse){
		return new GithubFollowUser(
			githubFollowUserResponse.id(),
			githubFollowUserResponse.login(),
			githubFollowUserResponse.avatarUrl()
		);
	}

	// id ASC
	@Override
	public int compareTo(GithubFollowUser otherUser) {
		return this.id.compareTo(otherUser.id);
	}
}
