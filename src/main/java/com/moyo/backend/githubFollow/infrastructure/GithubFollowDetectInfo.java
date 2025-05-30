package com.moyo.backend.githubFollow.infrastructure;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;
import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_MIN_REQUEST_THRESHOLD;

@Getter
public class GithubFollowDetectInfo {

    private int maxFollowingPage;
    private int maxFollowerPage;
    private int rateLimitRemaining;


    @Builder(access = AccessLevel.PRIVATE)
    private GithubFollowDetectInfo(int maxFollowingPage, int maxFollowerPage, int rateLimitRemaining) {
        this.maxFollowingPage = maxFollowingPage;
        this.maxFollowerPage = maxFollowerPage;
        this.rateLimitRemaining = rateLimitRemaining;
    }


    /**
     *   깃허브 기본 페이지는 1부터 시작 (즉, 0 == 1, 같은 페이지)
     *
     *   ex) following : 167, follower : 255, PagingSize = 100
     *       MaxFollowingPage : 167 / 100 + 1 => 2
     *       MaxFollowerPage : 255 / 100 + 1 => 3
     */
    public static GithubFollowDetectInfo from(GithubFollowStatsResponse response){

        return new GithubFollowDetectInfoBuilder()
                .maxFollowingPage(response.getFollowingCnt() / GITHUB_FOLLOW_QUERY_PAGING_SIZE + 1)
                .maxFollowerPage(response.getFollowerCnt() / GITHUB_FOLLOW_QUERY_PAGING_SIZE + 1)
                .rateLimitRemaining(response.getRateLimitRemaining())
                .build();
    }

    public boolean canFollowFetchRequest(){
        return (rateLimitRemaining - maxFollowerPage - maxFollowingPage) > GITHUB_MIN_REQUEST_THRESHOLD;
    }
}
