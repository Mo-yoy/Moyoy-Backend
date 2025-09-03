package com.moyoy.infra.external.github.repo;

import static com.moyoy.common.constant.MoyoConstants.GITHUB_MAX_QUERY_PAGING_SIZE;
import static com.moyoy.common.util.ThreadUtils.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubRepoClientImpl implements GithubRepoClient{

	private final GithubRepoFeignClient githubRepoFeignClient;
	private final ObjectMapper objectMapper;

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "repoFetchFallBack")
	public List<GithubRepoResponse> fetchReposCreatedThisYear(String accessToken) {

		final String affiliation = "owner,organization_member";

		final String since = LocalDate.now()
			.withDayOfYear(1)
			.atStartOfDay()
			.format(DateTimeFormatter.ISO_DATE_TIME);

		List<GithubRepoResponse> repoResponseList = new ArrayList<>();
		int page = 1;

		List<GithubRepoResponse> reposPage;

		do {
			reposPage = githubRepoFeignClient.fetchPagedRepos(
				affiliation,
				since,
				GITHUB_MAX_QUERY_PAGING_SIZE,
				page,
				accessToken
			);

			repoResponseList.addAll(reposPage);
			page++;

		} while (reposPage.size() == GITHUB_MAX_QUERY_PAGING_SIZE);

		return repoResponseList;
	}

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "repoFetchFallBack")
	public List<GithubRepoContributorsResponse> fetchRepoContributors(String accessToken, String repoFullName) {

		return githubRepoFeignClient.fetchRepoContributors(repoFullName, accessToken);
	}

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "statFetchFallBack")
	public List<GithubRepoCommitStatsResponse> fetchRepoContributorStats(String repoFullName, String accessToken) {

		int maxTryCount = 10;
		Response response = null;

		for (int tryCount = 1; tryCount <= maxTryCount; tryCount++) {

			response = githubRepoFeignClient.fetchContributorCommitActivity(repoFullName, accessToken);

			int status = response.status();

			if (status == 200) {

				log.info("Repo [{}] 통계 데이터 {}번째 시도에 성공", repoFullName, tryCount);
				break;
			} else if (status == 202) {

				log.info("Repo [{}] 통계 준비 중 ({}번째 시도). 10초 후 재시도", repoFullName, tryCount);
				sleep(10000);
			} else {
				throw new RuntimeException(
					String.format("Repo [%s] 통계 수집 중 오류 발생 (status=%d)", repoFullName, status)
				);
			}
		}

		if (response.status() != 200) {
			throw new RuntimeException("Repo [" + repoFullName + "] 통계 데이터를 끝내 불러오지 못했습니다.");
		}

		return parseContributorStatsResponse(response, repoFullName);
	}

	private void repoFetchFallBack(Long userId, Integer githubUserId, Throwable throwable) {
		if(throwable instanceof CallNotPermittedException){

		}
		/// TODO : 에러코드 회의 후 처리
	}

	private void statFetchFallBack(Long userId, Integer githubUserId, Throwable throwable) {
		if(throwable instanceof CallNotPermittedException){

		}
		/// TODO : 에러코드 회의 후 처리
	}

	private List<GithubRepoCommitStatsResponse> parseContributorStatsResponse(Response response, String repoFullName) {
		try {
			return objectMapper.readValue(
				response.body().asInputStream(),
				new TypeReference<List<GithubRepoCommitStatsResponse>>() {}
			);
		} catch (IOException e) {
			throw new RuntimeException("Repo [" + repoFullName + "] JSON 파싱 실패", e);
		}
	}
}
