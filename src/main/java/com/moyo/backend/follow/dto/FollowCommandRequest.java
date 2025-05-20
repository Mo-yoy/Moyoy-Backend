package com.moyo.backend.follow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowCommandRequest {

    private Long currentUserId;
    private String currentUserPrincipalName;
    private String targetUsername;
}
