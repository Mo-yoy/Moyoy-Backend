package com.moyoy.api.github_follow.presentation;

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
import com.moyoy.api.github_follow.application.GithubFollowService;
import com.moyoy.api.github_follow.application.request.GithubFollowDetectionData;
import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;
import com.moyoy.api.github_follow.presentation.response.GithubFollowDetectResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GithubFollowController {

	private final GithubFollowService githubFollowService;

	@GetMapping("/users/me/followings/{detectType}")
	public ResponseEntity<ApiResponse<GithubFollowDetectResponse>> detectGithubFollowRelation(
		@LoginUserId Long currentUserId,
		@PathVariable("detectType") String detectType,
		@RequestParam(value = "lastGithubUserId", required = false, defaultValue = "0") Integer lastGithubUserId,
		@RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize,
		@RequestParam(value = "forceSync", required = false) boolean forceSync) {

		GithubFollowDetectionData data = GithubFollowDetectionData.of(detectType, lastGithubUserId, pageSize, forceSync);
		GithubFollowDetectionResult result = githubFollowService.detect(currentUserId, data);

		GithubFollowDetectResponse response = GithubFollowDetectResponse.from(result);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/follow/{targetGithubUserId}")
	public ResponseEntity<ApiResponse<Void>> followGithubUser(
		@LoginUserId Long currentUserId,
		@PathVariable("targetGithubUserId") Integer targetGithubUserId) {

		githubFollowService.follow(currentUserId, targetGithubUserId);

		return ResponseEntity.ok(ApiResponse.noContent());
	}

	@DeleteMapping("/unfollow/{targetGithubUserId}")
	public ResponseEntity<ApiResponse<Void>> unFollowGithubUser(
		@LoginUserId Long currentUserId,
		@PathVariable("targetGithubUserId") Integer targetGithubUserId) {

		githubFollowService.unfollow(currentUserId, targetGithubUserId);

		return ResponseEntity.ok(ApiResponse.noContent());
	}
}
