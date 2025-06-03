package com.moyo.backend.githubFollow.dto;

import com.moyo.backend.githubFollow.domain.DetectType;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class GithubFollowDetectRequest {

    private DetectType detectType;
    private Long lastFetchedUserId;
    private int pagingSize;
    private boolean forceSync;
}
