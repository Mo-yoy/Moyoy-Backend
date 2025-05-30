package com.moyo.backend.githubFollow.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubFollowUser(@JsonProperty("id") Long id,
                               @JsonProperty("login") String username,
                               @JsonProperty("avatar_url") String profileImgUrl) {
}
