package com.moyoy.api.user.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyoy.api.common.annotation.LoginUserId;
import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.api.user.application.UserService;
import com.moyoy.api.user.application.response.UserProfileResult;
import com.moyoy.api.user.presentation.response.UserProfileResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

	private final UserService userService;

	@GetMapping("/users/me/profile")
	public ResponseEntity<ApiResponse<UserProfileResponse>> userProfile(
		@LoginUserId Long userId) {

		UserProfileResult userProfileResult = userService.getUserProfile(userId);

		UserProfileResponse responseData = UserProfileResponse.from(userProfileResult);

		return ResponseEntity.ok(ApiResponse.success(responseData));
	}
}
