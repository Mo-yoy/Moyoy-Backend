package com.moyo.backend.follow.presentation;

import com.moyo.backend.common.annotation.CurrentUserId;
import com.moyo.backend.common.annotation.LastFetchedUserId;
import com.moyo.backend.common.annotation.ValidPageSize;
import com.moyo.backend.common.dto.ApiResponse;
import com.moyo.backend.follow.application.GithubFollowService;
import com.moyo.backend.follow.domain.DetectType;
import com.moyo.backend.follow.dto.request.GithubFollowDetectRequest;
import com.moyo.backend.follow.dto.response.GithubFollowDetectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GithubFollowController {

    private final GithubFollowService githubFollowService;


    @GetMapping("/users/me/followings/{detectType}")
    public ResponseEntity<ApiResponse<GithubFollowDetectResponse>> getFollowUserList(@CurrentUserId Long currentUserId,
                                                                                     @PathVariable("detectType") DetectType detectType,
                                                                                     @RequestParam(value = "lastUserId", required = false, defaultValue = "0") @LastFetchedUserId Long lastUserId,
                                                                                     @RequestParam(value = "pageSize", required = false, defaultValue = "10") @ValidPageSize int pageSize){


        GithubFollowDetectRequest request = GithubFollowDetectRequest.builder()
                .lastFetchedUserId(lastUserId)
                .detectType(detectType)
                .pagingSize(pageSize)
                .build();

        long startTime = System.currentTimeMillis();
        GithubFollowDetectResponse response = githubFollowService.detectFollowUserList(currentUserId, request);
        log.info("개발용 로그 동기식 맞팔 탐지기 요청 소요 시간 : {}",System.currentTimeMillis() - startTime);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/users/me/followings/cache/clear")
    public ResponseEntity<ApiResponse<Void>> clearFollowRelationCache(@CurrentUserId Long currentUserId){

        githubFollowService.clearFollowCache(currentUserId);

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> followGithubUser(@CurrentUserId Long currentUserId,
                                                              @PathVariable("targetUserId") Long targetUserId){

        githubFollowService.follow(currentUserId, targetUserId);

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/unfollow/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> unFollowGithubUser(@CurrentUserId Long currentUserId,
                                                                @PathVariable("targetUserId") Long targetUserId){

        githubFollowService.unfollow(currentUserId, targetUserId);

        return ResponseEntity.ok(ApiResponse.noContent());
    }
}