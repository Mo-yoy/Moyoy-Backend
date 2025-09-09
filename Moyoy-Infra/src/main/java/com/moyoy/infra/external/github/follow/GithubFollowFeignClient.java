package com.moyoy.infra.external.github.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.moyoy.infra.external.github.user.GithubUserResponse;
import com.moyoy.infra.external.support.config.GithubFeignConfig;

import feign.Response;

@FeignClient(name = "githubFollowClient", url = "https://api.github.com", configuration = GithubFeignConfig.class)
interface GithubFollowFeignClient {

	@GetMapping("/user/followers")
	List<GithubUserResponse> fetchPagedFollowers(
		@RequestHeader(AUTHORIZATION) String bearer,
		@RequestParam("per_page") int perPage,
		@RequestParam("page") int page);

	@GetMapping("/user/following")
	List<GithubUserResponse> fetchPagedFollowings(
		@RequestHeader(AUTHORIZATION) String bearer,
		@RequestParam("per_page") int perPage,
		@RequestParam("page") int page);

	@PutMapping("/user/following/{username}")
	Response follow(
		@RequestHeader(AUTHORIZATION) String bearer,
		@PathVariable("username") String username);

	@DeleteMapping("/user/following/{username}")
	Response unfollow(
		@RequestHeader(AUTHORIZATION) String bearer,
		@PathVariable("username") String username);
}
