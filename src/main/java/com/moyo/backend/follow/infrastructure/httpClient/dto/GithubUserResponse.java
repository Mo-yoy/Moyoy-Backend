package com.moyo.backend.follow.infrastructure.httpClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GithubUserResponse {
    private Long id;
    private String login;
    private String avatarUrl;

    public GithubUserResponse(@JsonProperty("id") Long id,
                              @JsonProperty("login") String login,
                              @JsonProperty("avatar_url") String avatarUrl) {
        this.id = id;
        this.login = login;
        this.avatarUrl = avatarUrl;
    }
}