package com.moyo.backend.githubfollow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserFollowStats {

    private final Integer followingCnt;
    private final Integer followerCnt;

    public UserFollowStats(@JsonProperty("following") Integer followingCnt,
                           @JsonProperty("followers") Integer followerCnt) {

        this.followingCnt = followingCnt;
        this.followerCnt = followerCnt;
    }
}
