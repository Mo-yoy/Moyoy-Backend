package com.moyo.backend.githubFollow.exception;

import com.moyo.backend.common.exception.MoyoException;

import static com.moyo.backend.githubFollow.exception.FollowErrorCode.LIMIT_EXCEED;

public class GithubRateLimitExceedException extends MoyoException {
    public GithubRateLimitExceedException() {
        super(LIMIT_EXCEED);
    }
}
