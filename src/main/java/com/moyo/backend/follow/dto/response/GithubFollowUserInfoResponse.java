package com.moyo.backend.follow.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GithubFollowUserInfoResponse {

    private final Long id;
    private final String username;
    private final String profileImgUrl;

    public GithubFollowUserInfoResponse(@JsonProperty("id")Long id,
                                        @JsonProperty("login") String login,
                                        @JsonProperty("avatar_url") String avatarUrl) {
        this.id = id;
        this.username = login;
        this.profileImgUrl = avatarUrl;
    }
}