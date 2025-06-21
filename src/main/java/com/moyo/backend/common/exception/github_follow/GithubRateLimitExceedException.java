package com.moyo.backend.common.exception.github_follow;

import static com.moyo.backend.common.exception.github_follow.FollowErrorCode.LIMIT_EXCEED;

import com.moyo.backend.common.exception.MoyoException;

public class GithubRateLimitExceedException extends MoyoException {
	public GithubRateLimitExceedException() {
		super(LIMIT_EXCEED);
	}
}
