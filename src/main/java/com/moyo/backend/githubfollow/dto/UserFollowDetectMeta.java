package com.moyo.backend.githubfollow.dto;

import lombok.Builder;
import lombok.Getter;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;
import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_MIN_REQUEST_THRESHOLD;


/**
 *   깃허브 기본 페이지는 1부터 시작 (즉, 0 == 1, 같은 페이지)
 *
 *   ex) following : 167, follower : 255, PagingSize = 100
 *       MaxFollowingPage : 167 / 100 + 1 => 2
 *       MaxFollowerPage : 255 / 100 + 1 => 3
 */
@Getter
public class UserFollowDetectMeta {

    private final Integer maxFollowingPage;
    private final Integer maxFollowerPage;
    private final Integer rateLimitRemaining;

    @Builder
    public UserFollowDetectMeta(int followingCnt, int followerCnt, int rateLimitRemaining) {

        this.maxFollowingPage = followingCnt / GITHUB_FOLLOW_QUERY_PAGING_SIZE + 1;
        this.maxFollowerPage = followerCnt / GITHUB_FOLLOW_QUERY_PAGING_SIZE + 1;

        if (rateLimitRemaining - (maxFollowerPage + maxFollowingPage) < GITHUB_MIN_REQUEST_THRESHOLD)
            throw new RuntimeException("깃허브에 너무 많은 요청을 보내고 있습니다.");

        this.rateLimitRemaining = rateLimitRemaining;
    }
}