package com.moyo.backend.githubfollow.controller;

import com.moyo.backend.common.dto.ApiResponse;
import com.moyo.backend.githubfollow.dto.FollowCommandRequest;
import com.moyo.backend.githubfollow.dto.FollowDetectRequest;
import com.moyo.backend.githubfollow.dto.FollowDetectResponse;
import com.moyo.backend.githubfollow.service.GithubFollowService;
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
    public ResponseEntity<ApiResponse<FollowDetectResponse>> getFollowUserList(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
                                                            @PathVariable("detectType") String detectType,
                                                            @RequestParam(value = "lastUserId", required = false, defaultValue = "0") Long lastUserId,
                                                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize){

        FollowDetectRequest request = FollowDetectRequest.builder()
                .lastUserId(lastUserId)
                .detectType(detectType)
                .currentUserId(userPrincipal.getId())
                .pagingSize(pageSize)
                .currentUserPrincipalName(userPrincipal.getName())
                .build();

        long startTime = System.currentTimeMillis();
        FollowDetectResponse response = githubFollowService.detectFollowUserList(request);

        log.info("동기식 맞팔 탐지기 요청 소요 시간 : {}",System.currentTimeMillis() - startTime);

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