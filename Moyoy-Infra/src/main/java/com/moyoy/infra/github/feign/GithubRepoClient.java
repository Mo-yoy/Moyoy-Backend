package com.moyoy.infra.github.feign;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.moyoy.infra.config.GithubFeignConfig;
import com.moyoy.infra.github.dto.GithubRepoContributorsResponse;
import com.moyoy.infra.github.dto.GithubRepoResponse;

@FeignClient(
	name = "githubRepoClient",
	url = "https://api.github.com",
	configuration = GithubFeignConfig.class
)
public interface GithubRepoClient {

	@GetMapping("/user/repos")
	List<GithubRepoResponse> fetchPagedRepos(
		@RequestParam("affiliation") String affiliation,
		@RequestParam("since") String since,
		@RequestParam("per_page") int perPage,
		@RequestParam("page") int page,
		@RequestHeader(AUTHORIZATION) String accessToken
	);

	@GetMapping("/repos/{fullName}/contributors")
	List<GithubRepoContributorsResponse> fetchRepoContributors(
		@PathVariable("fullName") String repoFullName,
		@RequestHeader(AUTHORIZATION) String accessToken
	);

	///  해당 API는 202 와 비어있는 본문을 먼저 주고
	///  데이터가 준비되면 재호출 시 200과 데이터를 줌
	///  여기서 특정 Response로 파싱하게 되면 202가 올떄 에러남.
	///  일단 String으로 받고 Reader에서 직접 필요한 데이터 뽑아 써야 함.
	@GetMapping("/repos/{repoFullName}/stats/contributors")
	ResponseEntity<String> fetchContributorCommitActivity(
		@PathVariable("repoFullName") String repoFullName,
		@RequestHeader(AUTHORIZATION) String accessToken
	);

}