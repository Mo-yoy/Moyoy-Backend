package com.moyo.backend.follow.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FollowDetectResponse {

    private List<GithubFollowUser> githubFollowUserList;
    private boolean lastPage;
}