package com.moyo.backend.githubfollow.dto;

import lombok.Getter;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_MIN_REQUEST_THRESHOLD;

@Getter
public class UserFollowCommandMeta {

    private final Integer rateLimitRemaining;

    public UserFollowCommandMeta(Integer rateLimitRemaining) {
        if (rateLimitRemaining < GITHUB_MIN_REQUEST_THRESHOLD)
            throw new RuntimeException("깃허브에 너무 많은 요청을 보내고 있습니다.");

        this.rateLimitRemaining = rateLimitRemaining;
    }
}
