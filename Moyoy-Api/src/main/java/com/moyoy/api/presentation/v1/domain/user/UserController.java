package com.moyoy.api.presentation.v1.domain.user;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyoy.api.support.annotation.LoginUserId;
import com.moyoy.api.support.response.ApiResponse;
import com.moyoy.core.domain.user.business.UserProfileResult;
import com.moyoy.core.domain.user.business.UserService;

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
