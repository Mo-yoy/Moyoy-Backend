package com.moyoy.infra.external.github.user;

import static com.moyoy.common.constant.MoyoConstants.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.moyoy.infra.external.support.config.GithubFeignConfig;

import feign.Response;

@FeignClient(name = "githubProfileClient", url = "https://api.github.com", configuration = GithubFeignConfig.class)
public interface GithubUserFeignClient {

	@GetMapping("/user/{userId}")
	GithubUserResponse fetchUser(
		@RequestHeader(AUTHORIZATION) String accessToken,
		@PathVariable("userId") Integer githubUserId);

	@GetMapping("/user/{userId}")
	Response fetchUserRawResponse(
		@RequestHeader(AUTHORIZATION) String accessToken,
		@PathVariable("userId") Integer githubUserId);
}
