package com.moyoy.infra.github.feign;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.moyoy.infra.config.GithubFeignConfig;
import com.moyoy.infra.github.dto.GithubFollowUserResponse;

@FeignClient(
	name = "githubFollowClient",
	url = "https://api.github.com",
	configuration = GithubFeignConfig.class
)
public interface GithubFollowClient {

	@GetMapping("/user/followers")
	List<GithubFollowUserResponse> fetchPagedFollowers(
		@RequestParam("per_page") int perPage,
		@RequestParam("page") int page,
		@RequestHeader(AUTHORIZATION) String bearer
	);

	@GetMapping("/user/following")
	List<GithubFollowUserResponse> fetchPagedFollowings(
		@RequestParam("per_page") int perPage,
		@RequestParam("page") int page,
		@RequestHeader(AUTHORIZATION) String bearer
	);

	@PutMapping("/user/following/{username}")
	ResponseEntity<Void> follow(
		@PathVariable("username") String username,
		@RequestHeader(AUTHORIZATION) String bearer
	);

	@DeleteMapping("/user/following/{username}")
	ResponseEntity<Void> unfollow(
		@PathVariable("username") String username,
		@RequestHeader(AUTHORIZATION) String bearer
	);
}
