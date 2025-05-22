package com.moyo.backend.follow.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FollowDetectResponse {

    private List<GithubFollowUserInfoResponse> userList;
    private boolean lastPage;
    private int totalUserCount;
    private String lastSyncAt;
}