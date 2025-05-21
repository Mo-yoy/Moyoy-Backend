package com.moyo.backend.follow.exception;

import com.moyo.backend.common.exception.MoyoException;

import static com.moyo.backend.follow.exception.FollowErrorCode.LIMIT_EXCEED;

public class GithubRateLimitRemainingExceedException extends MoyoException {
    public GithubRateLimitRemainingExceedException() {
        super(LIMIT_EXCEED);
    }
}
