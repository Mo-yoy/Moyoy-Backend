package com.moyo.backend.githubFollow.exception;

import static com.moyo.backend.githubFollow.exception.FollowErrorCode.LIMIT_EXCEED;

import com.moyo.backend.common.exception.MoyoException;

public class GithubRateLimitExceedException extends MoyoException {
	public GithubRateLimitExceedException() {
		super(LIMIT_EXCEED);
	}
}
