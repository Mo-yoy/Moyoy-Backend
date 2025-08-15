package com.moyoy.api.follow.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moyoy.api.common.annotation.LoginUserId;
import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.api.follow.application.FollowService;
import com.moyoy.api.follow.application.request.GithubFollowDetection;
import com.moyoy.api.follow.application.response.GithubFollowDetectionResult;
import com.moyoy.api.follow.presentation.response.GithubFollowDetectResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GithubFollowController {

	private final FollowService followService;

	@GetMapping("/users/me/followings/{detectType}")
	public ResponseEntity<ApiResponse<GithubFollowDetectResponse>> getFollowUserList(
		@LoginUserId Long currentUserId,
		@PathVariable("detectType") String detectType,
		@RequestParam(value = "lastGithubUserId", required = false, defaultValue = "0") Integer lastGithubUserId,
		@RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize,
		@RequestParam(value = "forceSync", required = false) boolean forceSync) {

		GithubFollowDetection followDetection = new GithubFollowDetection(detectType, lastGithubUserId, pageSize, forceSync);
		GithubFollowDetectionResult followDetectionResult = followService.getFollowUserSlice(currentUserId, followDetection);

		GithubFollowDetectResponse responseData = GithubFollowDetectResponse.from(followDetectionResult);

		return ResponseEntity.ok(ApiResponse.success(responseData));
	}

	@PostMapping("/follow/{targetGithubUserId}")
	public ResponseEntity<ApiResponse<Void>> followGithubUser(
		@LoginUserId Long currentUserId,
		@PathVariable("targetGithubUserId") Integer targetGithubUserId) {

		followService.follow(currentUserId, targetGithubUserId);

		return ResponseEntity.ok(ApiResponse.noContent());
	}

	@DeleteMapping("/unfollow/{targetGithubUserId}")
	public ResponseEntity<ApiResponse<Void>> unFollowGithubUser(
		@LoginUserId Long currentUserId,
		@PathVariable("targetGithubUserId") Integer targetGithubUserId) {

		followService.unfollow(currentUserId, targetGithubUserId);

		return ResponseEntity.ok(ApiResponse.noContent());
	}
}
