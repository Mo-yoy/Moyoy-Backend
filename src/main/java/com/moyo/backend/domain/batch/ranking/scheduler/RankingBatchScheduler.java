package com.moyo.backend.domain.batch.ranking.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moyo.backend.common.implement.GithubOAuthTokenReader;
import com.moyo.backend.domain.batch.ranking.dto.GithubCommitStats;
import com.moyo.backend.domain.batch.ranking.dto.GithubRepoDetails;
import com.moyo.backend.domain.batch.ranking.dto.RankingPreflight;
import com.moyo.backend.domain.batch.ranking.dto.UserBatchSnapshot;
import com.moyo.backend.domain.batch.ranking.processor.CommitStatCalculator;
import com.moyo.backend.domain.batch.ranking.processor.GithubRepoClassifier;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculator;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculatorParameters;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculatorResult;
import com.moyo.backend.domain.batch.ranking.reader.RankingBatchReader;
import com.moyo.backend.domain.batch.ranking.reader.RepoContributorStats;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingReader;
import com.moyo.backend.domain.github_ranking.implement.RankingUpdater;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

	private static final int RANKING_BATCH_SIZE = 10;

	private final UserReader userReader;
	private final RankingBatchReader rankingBatchReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final RankingReader rankingReader;
	private final GithubRepoClassifier githubRepoClassifier;
	private final RankingCalculator rankingCalculator;
	private final RankingUpdater rankingUpdater;
	private final CommitStatCalculator commitStatCalculator;

	@Scheduled(cron = "10 53 01 * * *")
	public void rankingBatch() {

		LocalDateTime now = LocalDateTime.now();
		log.info("{} 랭킹 배치 작업 시작!", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

		UserBatchSnapshot userBatchSnapshot = userReader.getUserBatchSnapshot();
		log.info("{} 랭킹 배치 작업 | 총 유저 수 : {}", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), userBatchSnapshot.userCount());

		long lastUserId = 0;

		///  모든 User를 한 번에 메모리에 올릴 수는 없음
		while(lastUserId < userBatchSnapshot.lastUserId()){

			List<User> userList = userReader.findAll(lastUserId, RANKING_BATCH_SIZE);
			lastUserId = userList.getLast().getId();

			for (User user : userList) {

				Long currentUserId = user.getId();
				Integer currentGithubUserId = user.getGithubUserId();
				String githubAccessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

				// 1. 사용자 id로 username, follower 수, 소유 중인 개인 Repo 수, RateLimitRemaining 체크
				RankingPreflight rankingPreflight = rankingBatchReader.fetchRankingPreflight(currentGithubUserId, githubAccessToken);
				rankingPreflight.assertCanGithubRequest();

				// 2. 해당 사용자가 read, write, owner 권한을 가지고 있는 올해 Repo 모두 가져옴
				List<GithubRepoDetails> githubRepoDetailsList = rankingBatchReader.fetchAllGithubRepoDetails(githubAccessToken);

				// 3. Repo 중에서 사용자 소유의 Repo와 사용자가 기여한 Repo 선별
				String currentUsername = rankingPreflight.username();
				List<GithubRepoDetails> rankingCandidateRepos = githubRepoClassifier.classify(githubRepoDetailsList, currentUsername, githubAccessToken);

				// 4. 최종 필터링 된 Repo들 중에서 커밋 관련 데이터 획득 (엄청난 I/O 병목이 발생하는 구간)
				List<RepoContributorStats> userRepoContributorStatsList = new ArrayList<>();

				for (GithubRepoDetails repoDetails : rankingCandidateRepos) {

					List<RepoContributorStats> repoContributorStatsList = rankingBatchReader.fetchRepoContributorStats(repoDetails.repoName(), githubAccessToken);

					repoContributorStatsList.stream()
						.filter(contributor -> contributor.author().username().equals(currentUsername))
						.findFirst()
						.ifPresent(userRepoContributorStatsList::add);
				}

				GithubCommitStats commitStats = commitStatCalculator.calculateCommitStats(userRepoContributorStatsList);

				// 5. 파라미터 생성
				RankingCalculatorParameters rankingCalculatorParameters = RankingCalculatorParameters.of(rankingCandidateRepos, rankingPreflight,commitStats);

				// 6. 랭킹 계산
				RankingCalculatorResult rankingCalculatorResult = rankingCalculator.calculate(rankingCalculatorParameters);

				// 7. 랭킹 업데이트
				Ranking ranking = rankingReader.getRanking(currentUserId);
				ranking.updateRankingByBatch(rankingCalculatorResult);
				rankingUpdater.update(ranking);
			}
		}
	}

}
