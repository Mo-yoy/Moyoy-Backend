package com.moyo.backend.follow.presentation;

import com.moyo.backend.common.annotation.CurrentUserId;
import com.moyo.backend.common.annotation.LastFetchedUserId;
import com.moyo.backend.common.annotation.ValidPageSize;
import com.moyo.backend.common.dto.ApiResponse;
import com.moyo.backend.follow.application.GithubFollowService;
import com.moyo.backend.follow.domain.DetectType;
import com.moyo.backend.follow.dto.FollowCommandRequest;
import com.moyo.backend.follow.dto.request.GithubFollowDetectRequest;
import com.moyo.backend.follow.dto.response.FollowDetectResponse;
import com.moyo.backend.security.oauth.GithubOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GithubFollowController {

    private final GithubFollowService githubFollowService;


    @GetMapping("/users/me/followings/{detectType}")
    public ResponseEntity<ApiResponse<FollowDetectResponse>> getFollowUserList(@CurrentUserId Long currentUserId,
                                                                               @PathVariable("detectType") DetectType detectType,
                                                                               @RequestParam(value = "lastUserId", required = false, defaultValue = "0") @LastFetchedUserId Long lastUserId,
                                                                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") @ValidPageSize int pageSize){

        GithubFollowDetectRequest request = GithubFollowDetectRequest.builder()
                .lastFetchedUserId(lastUserId)
                .detectType(detectType.getValue())
                .pagingSize(pageSize)
                .build();

        long startTime = System.currentTimeMillis();
        FollowDetectResponse response = githubFollowService.detectFollowUserList(currentUserId, request);
        log.info("개발용 로그 동기식 맞팔 탐지기 요청 소요 시간 : {}",System.currentTimeMillis() - startTime);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/follow/{username}")
    public ResponseEntity<ApiResponse<Void>> followGithubUser(@AuthenticationPrincipal GithubOAuth2User githubOAuth2User,
                                                              @PathVariable("username") String username){

        FollowCommandRequest request = FollowCommandRequest.builder()
                .currentUserId(githubOAuth2User.getId())
                .currentUserPrincipalName(githubOAuth2User.getName())
                .targetUsername(username)
                .build();

        githubFollowService.follow(request);

        return ResponseEntity.ok().body(ApiResponse.noContent());
    }

    @DeleteMapping("/unfollow/{username}")
    public ResponseEntity<ApiResponse<Void>> unFollowGithubUser(@AuthenticationPrincipal GithubOAuth2User githubOAuth2User,
                                                                @PathVariable("username") String username){

        FollowCommandRequest request = FollowCommandRequest.builder()
                .currentUserId(githubOAuth2User.getId())
                .currentUserPrincipalName(githubOAuth2User.getName())
                .targetUsername(username)
                .build();

        githubFollowService.unfollow(request);

        return ResponseEntity.ok().body(ApiResponse.noContent());
    }
}