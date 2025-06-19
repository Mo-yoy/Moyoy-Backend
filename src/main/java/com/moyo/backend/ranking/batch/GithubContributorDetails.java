package com.moyo.backend.ranking.batch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubContributorDetails (
        @JsonProperty("login") String username
){
}
