package com.moyo.backend.githubfollow.dto;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class FollowDetectRequest {

    private int pagingSize;
    private String detectType;
    private Long lastUserId;
    private Long currentUserId;
    private String currentUserPrincipalName;

}
