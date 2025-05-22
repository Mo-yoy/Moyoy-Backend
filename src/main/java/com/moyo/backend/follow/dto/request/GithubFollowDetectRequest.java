package com.moyo.backend.follow.dto.request;

import com.moyo.backend.follow.domain.DetectType;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class GithubFollowDetectRequest {

    private DetectType detectType;
    private Long lastFetchedUserId;
    private int pagingSize;

}
