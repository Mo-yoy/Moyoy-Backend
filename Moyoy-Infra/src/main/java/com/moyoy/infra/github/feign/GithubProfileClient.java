package com.moyoy.infra.github.feign;

import static com.moyoy.common.constant.MoyoConstants.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.moyoy.infra.config.GithubFeignConfig;
import com.moyoy.infra.github.dto.GithubProfileResponse;

@FeignClient(
	name = "githubProfileClient",
	url = "https://api.github.com",
	configuration = GithubFeignConfig.class
)
public interface GithubProfileClient {

	@GetMapping("/user/{userId}")
	GithubProfileResponse fetchUserProfile(
		@RequestHeader(AUTHORIZATION) String accessToken,
		@PathVariable("userId") Integer githubUserId
	);

	@GetMapping("/user/{userId}")
	ResponseEntity<GithubProfileResponse> fetchUserProfileEntity(
		@RequestHeader(AUTHORIZATION) String accessToken,
		@PathVariable("userId") Integer githubUserId
	);
}
