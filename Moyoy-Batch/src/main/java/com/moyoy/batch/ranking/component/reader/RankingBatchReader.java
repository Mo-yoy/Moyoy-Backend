package com.moyoy.batch.ranking.component.reader;

import static com.moyoy.common.constant.MoyoConstants.*;
import static com.moyoy.common.util.ThreadUtils.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.moyoy.batch.ranking.component.dto.GithubContributorDetails;
import com.moyoy.batch.ranking.component.dto.GithubRepoDetails;
import com.moyoy.batch.ranking.component.dto.RepoContributorStats;
import com.moyoy.batch.ranking.component.dto.UserRankingProfile;
import com.moyoy.infra.github.dto.GithubProfileResponse;
import com.moyoy.infra.github.dto.GithubRepoContributorsResponse;
import com.moyoy.infra.github.dto.GithubRepoResponse;
import com.moyoy.infra.github.feign.GithubProfileClient;
import com.moyoy.infra.github.feign.GithubRepoClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchReader {

	private final GithubContributeStatsParser githubContributeStatsParser;
	private final GithubProfileClient githubProfileClient;
	private final GithubRepoClient githubRepoClient;

	public UserRankingProfile fetchUserRankingProfile(String accessToken, Integer githubUserId) {

		GithubProfileResponse githubProfileResponse = githubProfileClient.fetchUserProfile(accessToken, githubUserId);

		return UserRankingProfile.from(githubProfileResponse);
	}

	// 사용자가 소속된 Repo 개수가 100개가 넘지 않으면 한번에 처리됨.
	public List<GithubRepoDetails> fetchAllGithubRepoDetails(String accessToken) {

		List<GithubRepoResponse> githubRepoResponseList = new ArrayList<>();
		int currentPage = 1;

		String affiliation = "owner,organization_member";
		String since = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")) + "-01-01T00:00:00Z";

		while (true) {

			List<GithubRepoResponse> pagedRepos = githubRepoClient.fetchPagedRepos(affiliation, since, GITHUB_QUERY_PAGING_SIZE, currentPage, accessToken);
			githubRepoResponseList.addAll(pagedRepos);

			if (pagedRepos.size() < GITHUB_QUERY_PAGING_SIZE)
				break;
			currentPage++;
		}

		return githubRepoResponseList.stream()
			.map(GithubRepoDetails::from)
			.toList();
	}

	public List<GithubContributorDetails> fetchRepoContributors(String repoFullName, String accessToken) {

		List<GithubRepoContributorsResponse> githubRepoContributorsResponseList = githubRepoClient.fetchRepoContributors(repoFullName, accessToken);

		return githubRepoContributorsResponseList.stream()
			.map(GithubContributorDetails::from)
			.toList();
	}

	/// 깃허브 API에서 첫 요청에는 202 Accept 반환후 통계를 계산하고 실제 값이 준비 되면 200 OK와 Data를 넘겨줌, 같은 API인데 요청할 때 마다 응답 결과가 달라져서 직접 파싱
	public List<RepoContributorStats> fetchRepoContributorStats(String repoFullName, String accessToken) {

		int maxTryCount = 10;
		ResponseEntity<String> response = null;

		for (int tryCount = 1; tryCount <= maxTryCount; tryCount++) {

			response = githubRepoClient.fetchContributorCommitActivity(repoFullName, accessToken);

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
