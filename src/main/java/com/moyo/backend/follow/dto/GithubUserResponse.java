package com.moyo.backend.follow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubUserResponse(@JsonProperty("login") String login, @JsonProperty("avatar_url") String avatarUrl){
}