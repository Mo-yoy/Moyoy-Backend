package com.moyo.backend.githubFollow.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubFollowStatsResponse {

    private final Integer followingCnt;
    private final Integer followerCnt;
    private Integer rateLimitRemaining;

    public GithubFollowStatsResponse(@JsonProperty("following") Integer followingCnt,
                                     @JsonProperty("followers") Integer followerCnt) {

        this.followingCnt = followingCnt;
        this.followerCnt = followerCnt;
    }
}
