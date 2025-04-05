package com.moyo.backend.githubfollow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowCommandRequest {

    private Long currentUserId;
    private String currentUserPrincipalName;
    private String targetUsername;
    private Long targetUserId;
}
