package com.moyo.backend.domain.batchLegacy.ranking.implement;

import static com.moyo.backend.common.constant.MoyoConstants.*;
import static com.moyo.backend.common.util.ThreadUtils.*;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batchLegacy.ranking.data_access.GithubContributorDetailsResponse;
import com.moyo.backend.domain.batchLegacy.ranking.data_access.GithubProfileForRanking;
import com.moyo.backend.domain.batchLegacy.ranking.data_access.GithubRankingHttpClient;
import com.moyo.backend.domain.batchLegacy.ranking.data_access.GithubRepoDetailsResponse;
import com.moyo.backend.domain.batchLegacy.ranking.data_access.RepoContributorStats;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchReader {

	private final GithubContributeStatsParser githubContributeStatsParser;
	private final GithubRankingHttpClient githubRankingHttpClient;

	public UserRankingBatchSnapshot.RankingPreflight fetchRankingPreflight(Integer githubUserId, String accessToken) {

		ResponseEntity<GithubProfileForRanking> response = githubRankingHttpClient.fetchRankingPreflight(githubUserId, accessToken);
		int remainingRequestCount = Integer.parseInt(response.getHeaders().get("X-RateLimit-Remaining").getFirst());
		GithubProfileForRanking githubProfile = response.getBody();

		return new UserRankingBatchSnapshot.RankingPreflight(githubProfile.username(), githubProfile.followers(), remainingRequestCount);
	}

	// 성능 장애가 발생할 수 있지만 대부분의 사용자가 소속된 Repo의 개수가 100개를 넘지 않을 것으로 추정되어 넘어가도 될 듯함.
	public List<GithubRepoDetails> fetchAllGithubRepoDetails(String accessToken) {

		List<GithubRepoDetailsResponse> githubRepoDetailsResponseList = new ArrayList<>();
		int currentPage = 1;

		while (true) {
			List<GithubRepoDetailsResponse> pagedRepos = githubRankingHttpClient.fetchPagedRepos(currentPage, accessToken);
			githubRepoDetailsResponseList.addAll(pagedRepos);

			if (pagedRepos.size() < GITHUB_QUERY_PAGING_SIZE)
				break;
			currentPage++;
		}

		return githubRepoDetailsResponseList.stream()
			.map(GithubRepoDetails::from)
			.toList();
	}

	// 성능 장애가 발생할 수 있지만 대부분의 Repo의 Contributor수가 100을 넘지 않을 것으로 추정되어 넘어가도 될 듯함.
	public List<GithubContributorDetails> fetchRepoContributors(String repoFullName, String accessToken) {

		List<GithubContributorDetailsResponse> githubContributorDetailsResponseList = new ArrayList<>();
		int currentPage = 1;

		while (true) {
			List<GithubContributorDetailsResponse> pagedContributors = githubRankingHttpClient.fetchPagedContributors(currentPage, repoFullName, accessToken);
			githubContributorDetailsResponseList.addAll(pagedContributors);

			if (pagedContributors.size() < GITHUB_QUERY_PAGING_SIZE)
				break;
			currentPage++;
		}

		return githubContributorDetailsResponseList.stream()
			.map(GithubContributorDetails::from)
			.toList();
	}

	/// 깃허브 API에서 첫 요청에는 202 Accept 반환후 통계를 계산하고 실제 값이 준비 되면 200 OK와 Data를 넘겨줌, 같은 API인데 요청할 때 마다 응답 결과가 달라져서 직접 파싱
	public List<RepoContributorStats> fetchRepoContributorStats(String repoFullName, String accessToken) {

		int maxTryCount = 10;
		ResponseEntity<String> response = null;

		for (int tryCount = 1; tryCount <= maxTryCount; tryCount++) {

			response = githubRankingHttpClient.fetchContributorCommitActivity(repoFullName, accessToken);

			if (response.getStatusCode().value() == 200) {

				log.info("{}번째 시도에 성공", tryCount);
				break;
			} else if (response.getStatusCode().value() == 202) {
				sleep(10000);
			} else
				throw new RuntimeException("repo 통계 데이터 수집중 에러 발생");
		}

		return githubContributeStatsParser.parseGithubContributeStats(response);
	}
}
