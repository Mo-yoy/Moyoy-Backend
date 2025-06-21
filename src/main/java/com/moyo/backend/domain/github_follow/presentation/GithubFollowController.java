package com.moyo.backend.domain.github_follow.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moyo.backend.common.annotation.CurrentUserId;
import com.moyo.backend.common.response.ApiResponse;
import com.moyo.backend.domain.github_follow.business.GithubFollowDetection;
import com.moyo.backend.domain.github_follow.business.GithubFollowDetectionResult;
import com.moyo.backend.domain.github_follow.business.GithubFollowService;
import com.moyo.backend.domain.github_follow.presentation.dto.GithubFollowDetectResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GithubFollowController {

	private final GithubFollowService githubFollowService;

	@GetMapping("/users/me/followings/{detectType}")
	public ResponseEntity<ApiResponse<GithubFollowDetectResponse>> getFollowUserList(
		@CurrentUserId Long currentUserId,
		@PathVariable("detectType") String detectType,
		@RequestParam(value = "lastUserId", required = false, defaultValue = "0") Long lastUserId,
		@RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize,
		@RequestParam(value = "forceSync", required = false) boolean forceSync) {

		GithubFollowDetection followDetection = new GithubFollowDetection(detectType, lastUserId, pageSize, forceSync);
		GithubFollowDetectionResult followDetectionResult = githubFollowService.getFollowUserSlice(currentUserId, followDetection);

		GithubFollowDetectResponse responseData = GithubFollowDetectResponse.from(followDetectionResult);

		return ResponseEntity.ok(ApiResponse.success(responseData));
	}

	@PostMapping("/follow/{targetUserId}")
	public ResponseEntity<ApiResponse<Void>> followGithubUser(
		@CurrentUserId Long currentUserId,
		@PathVariable("targetUserId") Long targetUserId) {

		githubFollowService.follow(currentUserId, targetUserId);

		return ResponseEntity.ok(ApiResponse.noContent());
	}

	@DeleteMapping("/unfollow/{targetUserId}")
	public ResponseEntity<ApiResponse<Void>> unFollowGithubUser(
		@CurrentUserId Long currentUserId,
		@PathVariable("targetUserId") Long targetUserId) {

		githubFollowService.unfollow(currentUserId, targetUserId);

		return ResponseEntity.ok(ApiResponse.noContent());
	}
}
