package com.moyo.backend.domain.github_ranking.temporary_batch;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moyo.backend.common.exception.user.UserNotFoundException;
import com.moyo.backend.domain.auth.oauth.GithubOAuthTokenReader;
import com.moyo.backend.domain.user.data_access.UserRepository;
import com.moyo.backend.domain.user.implement.User;

@Service
@RequiredArgsConstructor
public class RankingTestBatchService {

	private final RankingCalculator rankingCalculator;
	private final UserRepository userRepository;
	private final RankingBatchReader rankingBatchReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;

	/**
	 *   일단 트랜잭션 스크립트 패턴으로 작성
	 *
	 *   추후 객체 분리 + 모듈 분리s
	 */
	@Transactional
	public void batchExec(Long currentUserId) {

		String accessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

		// 1. 사용자 id로 username, follower 수, 소유 중인 개인 Repo 수, RateLimitRemaining 체크
		RankingPreflight rankingPreflight = rankingBatchReader.getRankingPreflight(currentUserId, accessToken);
		String currentUsername = rankingPreflight.username();

		// 2. 해당 사용자의 2025년 Repo를 모두 가져옴
		List<GithubRepoDetails> allGithubRepoDetailsList = rankingBatchReader.getGithubRepoDetails(accessToken);

		// 3-1. 자신이 Owner인 repo 선별
		List<GithubRepoDetails> userRepos = allGithubRepoDetailsList.stream()
			.filter(repo -> repo.owner().name().equals(currentUsername))
			.toList();

		// 3-2. 자신이 Owner가 아닌 repo 선별
		List<GithubRepoDetails> organizationRepos = allGithubRepoDetailsList.stream()
			.filter(repo -> !repo.owner().name().equals(currentUsername))
			.toList();

		// 4. org repo 중에서 기여자 목록에 현재 사용자가 있는 repo만 필터링
		List<GithubRepoDetails> contributedOrgRepos = organizationRepos.stream()
			.filter(repo -> {
				List<GithubContributorDetails> contributorDetails = rankingBatchReader.getRepoContributors(repo.repoFullName(), accessToken);
				return contributorDetails.stream()
					.anyMatch(contributor -> currentUsername.equals(contributor.username()));
			})
			.toList();

		List<GithubRepoDetails> finalRankingRepoDetails = new ArrayList<>(userRepos);
		finalRankingRepoDetails.addAll(contributedOrgRepos);

		// 5. 최종적으로 필터링 된 Repo들 중에서 커밋 관련 데이터 획득
		List<GithubCommitStats> commitStats = new ArrayList<>();
		for (GithubRepoDetails repoDetails : finalRankingRepoDetails) {

			GithubCommitStats githubCommitStats = rankingBatchReader.getGithubCommitStats(repoDetails.repoFullName(), currentUsername, accessToken);
			commitStats.add(githubCommitStats);
		}

		// 6. 고정 파라미터 값 계산
		int starCount = finalRankingRepoDetails.stream()
			.mapToInt(GithubRepoDetails::startCount)
			.sum();

		int followerCount = rankingPreflight.followers();

		// 7-1. 주간 랭킹 계산
		int weekCommitCount = commitStats.stream()
			.mapToInt(commitStat -> commitStat.weekStats().commits())
			.sum();

		int weekCommitLines = commitStats.stream()
			.mapToInt(commitStat -> commitStat.weekStats().commitLines())
			.sum();

		long finalWeekRankingPoint = rankingCalculator.calculateRanking(weekCommitCount, weekCommitLines, starCount, followerCount).points();

		// 7-2. 월간 랭킹 계산
		int monthCommitCount = commitStats.stream()
			.mapToInt(commitStat -> commitStat.monthStats().commits())
			.sum();

		int monthCommitLines = commitStats.stream()
			.mapToInt(commitStat -> commitStat.monthStats().commitLines())
			.sum();

		long finalMonthRankingPoint = rankingCalculator.calculateRanking(monthCommitCount, monthCommitLines, starCount, followerCount).points();

		// 7-3. 연간 랭킹 계산
		int yearCommitCount = commitStats.stream()
			.mapToInt(commitStat -> commitStat.yearStats().commits())
			.sum();

		int yearCommitLines = commitStats.stream()
			.mapToInt(commitStat -> commitStat.yearStats().commitLines())
			.sum();

		RankingCalculateResult yearRankingResult = rankingCalculator.calculateRanking(yearCommitCount, yearCommitLines, starCount, followerCount);

		// 8. 랭킹 업데이트
		User currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
		currentUser.getRanking().updateRankingByBatch(yearRankingResult.level(), finalWeekRankingPoint * 7, finalMonthRankingPoint * 31, yearRankingResult.points() * 365);
	}
}
