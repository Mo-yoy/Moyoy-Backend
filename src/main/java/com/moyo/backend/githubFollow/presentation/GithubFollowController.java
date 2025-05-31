package com.moyo.backend.githubFollow.presentation;

import com.moyo.backend.githubFollow.application.GithubFollowCommandService;
import com.moyo.backend.githubFollow.application.GithubFollowRelationService;
import com.moyo.backend.security.annotation.CurrentUserId;
import com.moyo.backend.common.validation.annotation.LastFetchedUserId;
import com.moyo.backend.common.validation.annotation.ValidPageSize;
import com.moyo.backend.common.dto.ApiResponse;
import com.moyo.backend.githubFollow.domain.DetectType;
import com.moyo.backend.githubFollow.dto.GithubFollowDetectRequest;
import com.moyo.backend.githubFollow.dto.GithubFollowDetectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class GithubFollowController {

    private final GithubFollowRelationService githubFollowRelationService;
    private final GithubFollowCommandService githubFollowCommandService;

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

        return ResponseEntity.ok(ApiResponse.success(githubFollowRelationService.detectFollowUserList(currentUserId, request)));
    }

    @DeleteMapping("/users/me/followings/cache/clear")
    public ResponseEntity<ApiResponse<Void>> clearFollowRelationCache(@CurrentUserId Long currentUserId){

        githubFollowCommandService.clearFollowCache(currentUserId);

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> followGithubUser(@CurrentUserId Long currentUserId,
                                                              @PathVariable("targetUserId") Long targetUserId){

        githubFollowCommandService.follow(currentUserId, targetUserId);

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/unfollow/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> unFollowGithubUser(@CurrentUserId Long currentUserId,
                                                                @PathVariable("targetUserId") Long targetUserId){

        githubFollowCommandService.unfollow(currentUserId, targetUserId);

        return ResponseEntity.ok(ApiResponse.noContent());
    }
}