package com.moyo.backend.follow.ui;

import com.moyo.backend.common.model.ApiResponse;
import com.moyo.backend.follow.application.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{username}")
    public ResponseEntity<ApiResponse<Void>> follow(@PathVariable("username") String username, @AuthenticationPrincipal GithubOAuth2User userPrincipal){

        log.info("[팔로우 요청] | 요청자 ProviderId: {} -> 대상: {}", userPrincipal.getName(), username);

        followService.follow(username, userPrincipal);

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/unfollow/{username}")
    public ResponseEntity<ApiResponse<Void>> unfollow(@PathVariable("username") String username, @AuthenticationPrincipal GithubOAuth2User userPrincipal){

        log.info("[언 팔로우 요청] | 요청자 ProviderId: {} -> 대상: {}", userPrincipal.getName(), username);

        followService.unfollow(username, userPrincipal);

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    // 나만 상대를 팔로잉
    @GetMapping("/users/me/followings/only")
    public ResponseEntity<?> getFollowingOnlyList(@AuthenticationPrincipal GithubOAuth2User userPrincipal, Pageable pageable){

        return ResponseEntity.ok(ApiResponse.success(followService.getFollowingOnlyList(userPrincipal)));
    }

    // 상대만 나를 팔로잉
    @GetMapping("/users/me/followers/only")
    public ResponseEntity<?> getFollowerOnlyList(@AuthenticationPrincipal GithubOAuth2User userPrincipal, Pageable pageable){

        return ResponseEntity.ok(ApiResponse.success(followService.getFollowerOnlyList(userPrincipal)));
    }

    // 맞 팔로우
    @GetMapping("/users/me/followings/mutual")
    public ResponseEntity<?> getMutualFollowingList(@AuthenticationPrincipal GithubOAuth2User userPrincipal, Pageable pageable){

        return ResponseEntity.ok(ApiResponse.success(followService.getMutualFollowList(userPrincipal)));
    }

}
