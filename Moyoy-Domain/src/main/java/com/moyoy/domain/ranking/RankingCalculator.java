package com.moyoy.domain.ranking;

import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.dto.RankingCalculatorParameters;
import com.moyoy.domain.ranking.dto.RankingCalculatorResult;

@Component
public class RankingCalculator {

	/// 각 평가 항목의 중앙값과 가중치 설정
	private static final double COMMITS_MEDIAN = 300.0, COMMITS_WEIGHT = 3.0;
	private static final double COMMITS_LINES_MEDIAN = 30000.0, COMMITS_LINES_WEIGHT = 5.0;
	private static final double STARS_MEDIAN = 10.0, STARS_WEIGHT = 1.0;
	private static final double FOLLOWERS_MEDIAN = 100.0, FOLLOWERS_WEIGHT = 1.0;

	/// 전체 가중치 총합 (10)
	private static final double TOTAL_WEIGHT = COMMITS_WEIGHT + COMMITS_LINES_WEIGHT + STARS_WEIGHT + FOLLOWERS_WEIGHT;

	/// 누적 백분위로 등급을 정하기 위한 기준
	private static final double[] THRESHOLDS = {1, 12.5, 25, 37.5, 50, 62.5, 75, 87.5, 100};
	private static final String[] LEVELS = {"S", "A+", "A", "A-", "B+", "B", "B-", "C+", "C"};

	public RankingCalculatorResult calculate(RankingCalculatorParameters rankingParameters) {

		int starCount = rankingParameters.stars();
		int followerCount = rankingParameters.followers();

		long weekPoint = calculateRankingPoint(rankingParameters.weekStats().commits(), rankingParameters.weekStats().commitLines(), starCount, followerCount, RankingPeriod.WEEK);
		long monthPoint = calculateRankingPoint(rankingParameters.monthStats().commits(), rankingParameters.monthStats().commitLines(), starCount, followerCount, RankingPeriod.MONTH);
		long yearPoint = calculateRankingPoint(rankingParameters.yearStats().commits(), rankingParameters.yearStats().commitLines(), starCount, followerCount, RankingPeriod.YEAR);
		String grade = calculateRankingGrade(rankingParameters.yearStats().commits(), rankingParameters.yearStats().commitLines(), starCount, followerCount);

		return new RankingCalculatorResult(weekPoint, monthPoint, yearPoint, grade);
	}

	/**
	 *  특정 사용자의 GitHub 활동을 기반으로 랭킹 점수를 계산합니다.
	 *
	 *  랭킹 산정 기준:
	 *  - 사용자 소유의 레포지토리 및 사용자가 속한 조직에서의 실제 커밋 개수
	 *  - 사용자 소유의 레포지토리 및 사용자가 속한 조직에서 커밋한 코드 줄 수 (커밋 개수보다 더 중요한 요소)
	 *  - 사용자 소유의 레포지토리 및 사용자가 속한 조직에서 실제로 기여한 레포지토리의 스타 수 (상대적으로 낮은 가중치)
	 *  - 사용자의 팔로워 수 (상대적으로 낮은 가중치)
	 *
	 *  @param commits 총 커밋 수
	 *  @param commitLines 총 커밋 코드 라인 수
	 *  @param stars 총 star 개수
	 *  @param followers 사용자의 팔로워 수
	 */
	private String calculateRankingGrade(int commits, int commitLines, int stars, int followers) {

		double percentile = calculatePercentile(commits, commitLines, stars, followers);
		for (int i = 0; i < THRESHOLDS.length; i++) {
			if (percentile <= THRESHOLDS[i]) {
				return LEVELS[i];
			}
		}
		return "C";
	}

	private long calculateRankingPoint(int commits, int commitLines, int stars, int followers, RankingPeriod duration) {

		double percentile = calculatePercentile(commits, commitLines, stars, followers);

		return (long)((100 - percentile) * 100_000 * duration.getWeight());
	}

	/// 공통 산식: 백분위(0~100) 계산
	private double calculatePercentile(int commits, int commitLines, int stars, int followers) {

		// 각 항목의 점수를 계산한 후 전체 가중치로 평균 → 1에서 빼면 "상대 점수 Percentile"
		double rank = 1 - (COMMITS_WEIGHT * exponentialCdf(commits / COMMITS_MEDIAN) +
			COMMITS_LINES_WEIGHT * logNormalCdf(commitLines / COMMITS_LINES_MEDIAN) +
			STARS_WEIGHT * exponentialCdf(stars / STARS_MEDIAN) +
			FOLLOWERS_WEIGHT * exponentialCdf(followers / FOLLOWERS_MEDIAN)) / TOTAL_WEIGHT;

		return rank * 100;
	}

	/// 지수 CDF → 초반에 민감 하게 점수 부여 (소수 정예를 띄우고 싶을 때)
	private double exponentialCdf(double x) {
		return 1 - Math.pow(2, -x);
	}

	/// 로그 CDF → 큰 값의 영향력을 줄이고 점수를 균형 있게 제한 하고 싶을 때
	private double logNormalCdf(double x) {
		return x / (1 + x);
	}
}
