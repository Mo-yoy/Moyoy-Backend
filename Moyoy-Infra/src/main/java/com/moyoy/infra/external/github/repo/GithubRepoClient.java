package com.moyoy.infra.external.github.repo;

import static com.moyoy.common.constant.MoyoConstants.GITHUB_MAX_QUERY_PAGING_SIZE;
import static com.moyoy.common.util.ThreadUtils.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import com.moyoy.infra.external.github.repo.dto.GithubRepoCommitStatsResponse;
import com.moyoy.infra.external.github.repo.dto.GithubRepoContributorsResponse;
import com.moyoy.infra.external.github.repo.dto.GithubRepoResponse;
import com.moyoy.infra.external.github.support.GithubResponseParser;
import com.moyoy.infra.external.github.support.error.GithubPollingApiTimeOutException;

import feign.Response;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubRepoClient {

	private final GithubRepoApi githubRepoApi;
	private final GithubResponseParser responseParser;

	public List<GithubRepoResponse> fetchReposCreatedThisYear(String bearerToken) {

		final String affiliation = "owner,organization_member";

		final String thisYearSince = LocalDate.now()
			.withDayOfYear(1)
			.atStartOfDay()
			.format(DateTimeFormatter.ISO_DATE_TIME);

		List<GithubRepoResponse> repoResponseList = new ArrayList<>();
		int currentPage = 1;

		List<GithubRepoResponse> reposPage;

		do {
			reposPage = githubRepoApi.fetchPagedRepos(
				bearerToken,
				affiliation,
				thisYearSince,
				GITHUB_MAX_QUERY_PAGING_SIZE,
				currentPage);

			repoResponseList.addAll(reposPage);
			currentPage++;

		} while (reposPage.size() == GITHUB_MAX_QUERY_PAGING_SIZE);

		return repoResponseList;
	}

	public List<GithubRepoContributorsResponse> fetchRepoContributors(String bearerToken, String repoFullName) {

		return githubRepoApi.fetchRepoContributors(bearerToken, repoFullName);
	}

	/**
	 *	Feign 호출 후 발생한 에러는 ErrorDecoder 에서 처리됨
	 */
	public List<GithubRepoCommitStatsResponse> fetchRepoContributorStats(String bearerToken, String repoFullName) throws GithubPollingApiTimeOutException {

		int maxTryCount = 10;
		Response response = null;

		for (int tryCount = 1; tryCount <= maxTryCount; tryCount++) {

			Response attempt = githubRepoApi.fetchContributorCommitActivity(bearerToken, repoFullName);
			int status = attempt.status();

			if (status == 200) {

				log.info("Repo [{}] 통계 {}번째 시도 성공", repoFullName, tryCount);
				response = attempt;
				break;
			} else if (status == 202) {

				attempt.close();
				log.info("Repo [{}] 통계 준비 중 ({}번째 시도). 10초 후 재시도", repoFullName, tryCount);
				sleep(10000);
			} else
				attempt.close();
		}

		if (response != null) {
			return responseParser.parseBody(response, new TypeReference<List<GithubRepoCommitStatsResponse>>() {});
		} else
			throw new GithubPollingApiTimeOutException();
	}
}
