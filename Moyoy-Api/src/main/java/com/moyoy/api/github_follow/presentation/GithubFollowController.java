package com.moyoy.api.github_follow.presentation;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyoy.api.common.annotation.LoginUserId;
import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.api.github_follow.application.GithubFollowCommandService;
import com.moyoy.api.github_follow.application.GithubFollowDetectService;
import com.moyoy.api.github_follow.application.request.GithubFollowDetectionData;
import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;
import com.moyoy.api.github_follow.presentation.request.GithubFollowCommandRequest;
import com.moyoy.api.github_follow.presentation.request.GithubFollowDetectRequest;
import com.moyoy.api.github_follow.presentation.response.GithubFollowDetectResponse;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GithubFollowController {

	private final GithubFollowDetectService githubFollowDetectService;
	private final GithubFollowCommandService githubFollowCommandService;

	@GetMapping("/users/me/followings/{detectType}")
	public ResponseEntity<ApiResponse<GithubFollowDetectResponse>> detectGithubFollowRelation(
		@LoginUserId Long currentUserId,
		@Valid GithubFollowDetectRequest request) {

		GithubFollowDetectionData data = GithubFollowDetectionData.of(request.detectType(), request.lastGithubUserId(), request.size());
		Optional<GithubFollowDetectionResult> result = githubFollowDetectService.detect(currentUserId, data);

		if (result.isEmpty()) {
			return ResponseEntity.accepted().body(ApiResponse.accepted());
		}

		GithubFollowDetectResponse response = GithubFollowDetectResponse.from(result.get());
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/users/me/followings/refresh")
	public ResponseEntity<ApiResponse<Void>> refreshGithubFollowRelation(
		@LoginUserId Long currentUserId) {

		githubFollowDetectService.refresh(currentUserId);

		return ResponseEntity.accepted().body(ApiResponse.accepted());
	}

	@PostMapping("/follow/{targetGithubUserId}")
	public ResponseEntity<ApiResponse<Void>> followGithubUser(
		@LoginUserId Long currentUserId,
		@Valid GithubFollowCommandRequest request) {

		githubFollowCommandService.follow(currentUserId, request.targetGithubUserId());

		return ResponseEntity.ok(ApiResponse.noContent());
	}

	@DeleteMapping("/unfollow/{targetGithubUserId}")
	public ResponseEntity<ApiResponse<Void>> unFollowGithubUser(
		@LoginUserId Long currentUserId,
		@Valid GithubFollowCommandRequest request) {

		githubFollowCommandService.unfollow(currentUserId, request.targetGithubUserId());

		return ResponseEntity.ok(ApiResponse.noContent());
	}
}
