package com.moyo.backend.githubFollow.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GithubFollowDetectResponse {

    private List<GithubFollowUserResponseDto> userList;
    private boolean lastPage;
    private int totalUserCount;
    private String lastSyncAt;

}