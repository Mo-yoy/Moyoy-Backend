package com.moyo.backend.domain.temporary_batch.ranking.reader;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_MIN_REQUEST_THRESHOLD;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyo.backend.common.exception.github_follow.GithubRateLimitExceedException;
import com.moyo.backend.domain.temporary_batch.ranking.dto.GithubCommitStats;
import com.moyo.backend.domain.temporary_batch.ranking.dto.GithubContributorDetails;
import com.moyo.backend.domain.temporary_batch.ranking.dto.GithubRepoDetails;
import com.moyo.backend.domain.temporary_batch.ranking.dto.RankingPreflight;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchReader {

	private final ObjectMapper objectMapper;
	private final GithubRankingHttpClient githubRankingHttpClient;

	public RankingPreflight getRankingPreflight(Long currentUserId, String accessToken) {

		ResponseEntity<RankingPreflight> response = githubRankingHttpClient.fetchRankingPreflight(currentUserId, accessToken);
		int remainingRequestCnt = Integer.parseInt(response.getHeaders().get("X-RateLimit-Remaining").getFirst());

		// 배치 작업에 정확히 몇번 요청이 필요할지 모르겠음, 사람마다 천차 만별이라 500 정도로 임시로 둠, 추후 조정
		if (remainingRequestCnt < 500 + GITHUB_MIN_REQUEST_THRESHOLD)
			throw new GithubRateLimitExceedException();

		return response.getBody();
	}

	// 성능 장애가 발생할 수 있지만 대부분의 사용자가 소속된 Repo의 개수가 100개를 넘지 않을 것으로 추정되어 넘어가도 될 듯함.
	public List<GithubRepoDetails> getGithubRepoDetails(String accessToken) {

		List<GithubRepoDetails> githubRepoDetailsList = new ArrayList<>();
		int currentPage = 1;

		while (true) {
			List<GithubRepoDetails> pagedRepos = githubRankingHttpClient.fetchPagedRepos(currentPage, accessToken);
			githubRepoDetailsList.addAll(pagedRepos);

			if (pagedRepos.size() < 100)
				break;
			currentPage++;
		}

		return githubRepoDetailsList;
	}

	public List<GithubContributorDetails> getRepoContributors(String repoFullName, String accessToken) {

		List<GithubContributorDetails> repoContributors = new ArrayList<>();
		int currentPage = 1;

		while (true) {
			List<GithubContributorDetails> pagedContributors = githubRankingHttpClient.fetchPagedContributors(currentPage, repoFullName, accessToken);
			repoContributors.addAll(pagedContributors);

			if (pagedContributors.size() < 100)
				break;
			currentPage++;
		}

		return repoContributors;
	}

	// 깃허브 API에서 첫 요청에는 202 Accept 반환후 통계를 계산하고 실제 값이 준비 되면 200 OK와 Data를 넘겨줌
	public GithubCommitStats getGithubCommitStats(String repoFullName, String currentUsername, String accessToken) {

		int maxTryCount = 10;
		ResponseEntity<?> response = null;

		for (int tryCount = 1; tryCount <= maxTryCount; tryCount++) {
			response = githubRankingHttpClient.fetchContributorCommitActivity(repoFullName, accessToken);

			if (response.getStatusCode().value() == 200)
				break;
			else if (response.getStatusCode().value() == 202) {

				log.info("{}번째 시도 중 | 깃허브 에서 커밋 통계 계산 중", tryCount);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (response.getStatusCode().value() != 200)
			throw new RuntimeException("repo 통계 데이터 수집중 에러 발생");

		List<ContributorStats> contributorStats;

		try {
			contributorStats = objectMapper.readValue((String)response.getBody(), new TypeReference<>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON 파싱 중 에러 발생", e);
		}

		// 파싱된 리스트에서 현재 사용자만 필터링
		List<ContributorStats> currentUserContributorStats = contributorStats.stream()
			.filter(contributor -> contributor.getAuthor().getUsername().equals(currentUsername))
			.toList();

		// 현재 날짜 기준, 시간대 설정
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDate today = LocalDate.now(zoneId);
		WeekFields weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1);
		int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
		int currentMonth = today.getMonthValue();
		int currentYear = today.getYear();

		int additionsThisWeek = 0;
		int commitsThisWeek = 0;

		int additionsThisMonth = 0;
		int commitsThisMonth = 0;

		int additionsThisYear = 0;
		int commitsThisYear = 0;

		for (ContributorStats contributor : currentUserContributorStats) {
			for (ContributorStats.Week week : contributor.getWeeks()) {
				long unixSeconds = week.getWeekTimeStamp();

				ZonedDateTime zonedDateTime = Instant.ofEpochSecond(unixSeconds).atZone(zoneId);

				int weekOfYear = zonedDateTime.get(weekFields.weekOfWeekBasedYear());
				int month = zonedDateTime.getMonthValue();
				int year = zonedDateTime.getYear();

				if (year == currentYear) {
					additionsThisYear += week.getAddCodeLine();
					commitsThisYear += week.getCommit();

					if (month == currentMonth) {
						additionsThisMonth += week.getAddCodeLine();
						commitsThisMonth += week.getCommit();

						if (weekOfYear == currentWeek) {
							additionsThisWeek += week.getAddCodeLine();
							commitsThisWeek += week.getCommit();
						}
					}
				}
			}
		}

		return new GithubCommitStats(
			new GithubCommitStats.WeekStats(commitsThisWeek, additionsThisWeek),
			new GithubCommitStats.MonthStats(commitsThisMonth, additionsThisMonth),
			new GithubCommitStats.YearStats(commitsThisYear, additionsThisYear));
	}
}
