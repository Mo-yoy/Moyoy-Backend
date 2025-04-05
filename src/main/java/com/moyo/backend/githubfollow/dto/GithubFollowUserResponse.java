package com.moyo.backend.githubfollow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GithubFollowUserResponse {

    private final Long id;
    private final String username;
    private final String profileImgUrl;

    public GithubFollowUserResponse(@JsonProperty("id")Long id,
                                    @JsonProperty("login") String login,
                                    @JsonProperty("avatar_url") String avatarUrl) {
        this.id = id;
        this.username = login;
        this.profileImgUrl = avatarUrl;
    }
}