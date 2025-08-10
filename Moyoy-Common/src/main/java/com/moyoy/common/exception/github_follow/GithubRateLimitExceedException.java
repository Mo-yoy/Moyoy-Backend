package com.moyoy.common.exception.github_follow;


import static com.moyoy.common.exception.github_follow.FollowErrorCode.*;

import com.moyoy.common.exception.MoyoException;

public class GithubRateLimitExceedException extends MoyoException {
	public GithubRateLimitExceedException() {
		super(LIMIT_EXCEED);
	}
}
