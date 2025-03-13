package com.moyo.backend.follow.ui;

import com.moyo.backend.common.model.ApiResponse;
import com.moyo.backend.follow.application.FollowService;
import com.moyo.backend.follow.ui.dto.FollowListResponse;
import com.moyo.backend.security.oauth.dto.GithubOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // 맞 팔로우 (Query)
    @GetMapping("/users/me/followings/mutual")
    public ResponseEntity<ApiResponse<FollowListResponse>> getMutualFollowingList(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
                                                                                  Pageable pageable){

        return ResponseEntity.ok(ApiResponse.success(followService.getMutualFollowingList(userPrincipal.getId(), pageable)));
    }

    // 상대만 나를 팔로잉 (Query)
    @GetMapping("/users/me/followers/only")
    public ResponseEntity<ApiResponse<FollowListResponse>> getFollowerOnlyList(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
                                                                               Pageable pageable){

        return ResponseEntity.ok(ApiResponse.success(followService.getFollowerOnlyList(userPrincipal.getId(), pageable)));
    }

    // 나만 상대를 팔로잉 (Query)
    @GetMapping("/users/me/followings/only")
    public ResponseEntity<ApiResponse<FollowListResponse>> getFollowingOnlyList(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
                                                                                Pageable pageable){

        return ResponseEntity.ok(ApiResponse.success(followService.getFollowingOnlyList(userPrincipal.getId(), pageable)));
    }
}