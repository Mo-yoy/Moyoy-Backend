package com.moyo.backend.follow.dto;

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
