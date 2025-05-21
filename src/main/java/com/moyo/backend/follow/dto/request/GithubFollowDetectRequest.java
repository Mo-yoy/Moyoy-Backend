package com.moyo.backend.follow.dto.request;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class GithubFollowDetectRequest {

    private String detectType;

    private Long lastFetchedUserId;
    private int pagingSize;

}
