package com.moyoy.domain.ranking;

import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.dto.RankingCalculatorParameters;
import com.moyoy.domain.ranking.dto.RankingCalculatorResult;

@Component
public class RankingCalculator {

	/**
	 * <h1>랭킹 산정 기준 상수</h1>
	 *
	 * <p>GitHub 활동 데이터를 점수화하고 등급을 매기기 위해 사용되는 기준값 모음.</p>
	 *
	 * <h2>Median (중앙값)</h2>
	 * <ul>
	 *   <li>연간(YEAR) 기준값을 정의하고, 월간(MONTH)·주간(WEEK)은 연간 값을 12, 52로 나눈 근사치</li>
	 *   <li>스타/팔로워는 누적 데이터이므로 기간 구분 없이 단일 Median 사용</li>
	 * </ul>
	 *
	 * <h2>Weight (가중치)</h2>
	 * <ul>
	 *   <li>각 지표가 랭킹 점수에 기여하는 비중</li>
	 *   <li>커밋 수와 커밋 라인 수는 기여도의 핵심 지표이므로 높은 가중치 부여</li>
	 *   <li>스타/팔로워는 외부 인지도를 반영하지만 상대적으로 낮은 가중치</li>
	 * </ul>
	 *
	 * <h2>Thresholds & Levels</h2>
	 * <ul>
	 *   <li>퍼센타일(0~100) 값에 따라 등급(Level)을 매핑하는 기준</li>
	 *   <li>{@code THRESHOLDS} : 누적 백분위 경계값 배열</li>
	 *   <li>{@code LEVELS} : 각 구간에 대응하는 등급 ("S" ~ "C")</li>
	 *   <li>예: 퍼센타일이 20 → THRESHOLDS[2]=25 이하 → LEVELS[2]="A"</li>
	 * </ul>
	 */

	private static final double YEARLY_COMMITS_MEDIAN = 100.0;
	private static final double MONTHLY_COMMITS_MEDIAN = YEARLY_COMMITS_MEDIAN / 12;
	private static final double WEEKLY_COMMITS_MEDIAN = YEARLY_COMMITS_MEDIAN / 52;

	private static final double YEARLY_COMMITS_LINES_MEDIAN = 5000.0;
	private static final double MONTHLY_COMMITS_LINES_MEDIAN = YEARLY_COMMITS_LINES_MEDIAN / 12;
	private static final double WEEKLY_COMMITS_LINES_MEDIAN = YEARLY_COMMITS_LINES_MEDIAN / 52;

	private static final double STARS_MEDIAN = 10.0;
	private static final double FOLLOWERS_MEDIAN = 30.0;

	private static final double COMMITS_WEIGHT = 3.0;
	private static final double COMMITS_LINES_WEIGHT = 5.0;
	private static final double STARS_WEIGHT = 1.0;
	private static final double FOLLOWERS_WEIGHT = 1.0;
	private static final double TOTAL_WEIGHT = COMMITS_WEIGHT + COMMITS_LINES_WEIGHT + STARS_WEIGHT + FOLLOWERS_WEIGHT;

	private static final double[] THRESHOLDS = {1, 12.5, 25, 37.5, 50, 62.5, 75, 87.5, 100};
	private static final String[] LEVELS = {"S", "A+", "A", "A-", "B+", "B", "B-", "C+", "C"};

	/**
	 * 주간/월간/연간 점수와 연간 등급을 계산한다.
	 *
	 * <p>계산 절차:</p>
	 * <ol>
	 *   <li>{@link #calculateRankingPoint} 를 호출하여
	 *       주간(WEEK), 월간(MONTH), 연간(YEAR) 각각의 점수를 산출</li>
	 *   <li>{@link #calculateRankingGrade} 를 호출하여
	 *       연간(YEAR) 기준으로만 최종 등급을 산출</li>
	 *   <li>세 개의 점수와 하나의 등급을 {@link RankingCalculatorResult} 에 담아 반환</li>
	 * </ol>
	 *
	 * <p><b>특징</b>:</p>
	 * <ul>
	 *   <li>점수는 모든 기간(주/월/년)에 대해 제공</li>
	 *   <li>등급은 연간 기준으로만 제공 (주/월은 점수만 산출)</li>
	 * </ul>
	 *
	 * @param rankingParameters GitHub 활동 지표 (주/월/년 단위 통계 포함)
	 * @return 주간/월간/연간 점수와 연간 등급을 담은 {@link RankingCalculatorResult}
	 */

	public RankingCalculatorResult calculate(RankingCalculatorParameters rankingParameters) {

		long weekPoint = calculateRankingPoint(rankingParameters, RankingPeriod.WEEK);
		long monthPoint = calculateRankingPoint(rankingParameters, RankingPeriod.MONTH);
		long yearPoint = calculateRankingPoint(rankingParameters, RankingPeriod.YEAR);

		String grade = calculateRankingGrade(rankingParameters);

		return new RankingCalculatorResult(weekPoint, monthPoint, yearPoint, grade);
	}

	/**
	 * 특정 기간(주간/월간/연간)의 GitHub 활동 지표를 기반으로 점수를 계산한다.
	 *
	 * <p>계산 절차:</p>
	 * <ol>
	 *   <li>기간(duration)에 맞는 커밋 수와 커밋 라인 수를 {@link RankingCalculatorParameters} 에서 추출</li>
	 *   <li>스타 수와 팔로워 수는 기간 구분 없이 누적 값을 사용</li>
	 *   <li>{@link #calculatePercentile} 메서드로 활동 지표 기반 백분위(0~100)를 계산</li>
	 *   <li>(100 - 백분위) * 10,000 * 기간 비율(duration.getWeight())로 점수를 산출</li>
	 *   <li>long으로 캐스팅하여 정수 점수를 반환</li>
	 * </ol>
	 *
	 * <p><b>특징</b>:</p>
	 * <ul>
	 *   <li>백분위가 낮을수록 (활동량 ↑) 점수가 높게 산출됨</li>
	 *   <li>기간별 비율(duration.getWeight())을 곱해 주간/월간/연간 점수를 상대적으로 보정</li>
	 *   <li>출력되는 점수는 정수(long) 단위로 관리됨</li>
	 * </ul>
	 *
	 * @param rankingParameters GitHub 활동 지표 (기간별/누적 데이터 포함)
	 * @param duration 계산 대상 기간 ({@link RankingPeriod})
	 * @return 산출된 점수(long)
	 */

	private long calculateRankingPoint(RankingCalculatorParameters rankingParameters, RankingPeriod duration) {

		int commits = switch (duration) {
			case WEEK -> rankingParameters.weekStats().commits();
			case MONTH -> rankingParameters.monthStats().commits();
			case YEAR -> rankingParameters.yearStats().commits();
		};

		int commitLines = switch (duration) {
			case WEEK -> rankingParameters.weekStats().commitLines();
			case MONTH -> rankingParameters.monthStats().commitLines();
			case YEAR -> rankingParameters.yearStats().commitLines();
		};

		int stars = rankingParameters.stars();
		int followers = rankingParameters.followers();

		double percentile = calculatePercentile(commits, commitLines, stars, followers, duration);

		return (long)((100 - percentile) * 100_00 * duration.getWeight());
	}

	/**
	 * 연간(YEAR) 활동 지표를 기반으로 최종 등급(Grade)을 계산한다.
	 *
	 * <p>계산 절차:</p>
	 * <ol>
	 *   <li>연간 커밋 수, 커밋 라인 수, 스타 수, 팔로워 수를 추출</li>
	 *   <li>{@link #calculatePercentile} 로 백분위(0~100)를 계산</li>
	 *   <li>백분위 값이 {@code THRESHOLDS} 배열의 어느 구간에 속하는지 판별</li>
	 *   <li>해당 구간의 {@code LEVELS} 값을 반환</li>
	 *   <li>모든 구간에 해당하지 않으면 기본값 "C" 반환</li>
	 * </ol>
	 *
	 * <p><b>특징</b>:</p>
	 * <ul>
	 *   <li>등급 산출은 연간(YEAR) 데이터만 사용</li>
	 *   <li>퍼센타일 값이 낮을수록 더 높은 등급(S, A 등)을 부여</li>
	 *   <li>퍼센타일 값이 높으면 낮은 등급(B, C 등)으로 매핑</li>
	 * </ul>
	 *
	 * @param rankingParameters GitHub 활동 지표 (연간 데이터 포함)
	 * @return 산출된 등급 문자열 ("S" ~ "C")
	 */
	private String calculateRankingGrade(RankingCalculatorParameters rankingParameters) {

		double percentile = calculatePercentile(
			rankingParameters.yearStats().commits(),
			rankingParameters.yearStats().commitLines(),
			rankingParameters.stars(),
			rankingParameters.followers(),
			RankingPeriod.YEAR);

		for (int i = 0; i < THRESHOLDS.length; i++) {
			if (percentile <= THRESHOLDS[i]) {
				return LEVELS[i];
			}
		}
		return "C";
	}

	/**
	 * 활동 지표와 기간을 기반으로 백분위(Percentile, 0~100)를 계산한다.
	 *
	 * <p>계산 절차:</p>
	 * <ol>
	 *   <li>기간(duration)에 따라 커밋 수와 커밋 라인 수의 Median 기준값을 선택</li>
	 *   <li>입력값(commits, commitLines, stars, followers)을 각 Median 값으로 정규화</li>
	 *   <li>정규화된 값에 지표별 CDF 함수 적용
	 *       <ul>
	 *         <li>커밋 수, 스타, 팔로워 → {@link #exponentialCdf(double)} (초기 활동량에 민감)</li>
	 *         <li>커밋 라인 수 → {@link #logNormalCdf(double)} (큰 값의 영향 완화)</li>
	 *       </ul>
	 *   </li>
	 *   <li>지표별 점수에 가중치(COMMITS_WEIGHT, COMMITS_LINES_WEIGHT 등)를 곱하고 총합</li>
	 *   <li>총합을 전체 가중치(TOTAL_WEIGHT)로 나누어 평균화</li>
	 *   <li>1에서 빼서 rank 계산 (활동량 ↑ → rank ↑ → percentile ↓)</li>
	 *   <li>rank를 0~100 범위로 변환하여 최종 백분위 반환</li>
	 * </ol>
	 *
	 * <p><b>특징</b>:</p>
	 * <ul>
	 *   <li>퍼센타일 값이 낮을수록 활동 성과가 높음을 의미</li>
	 *   <li>퍼센타일 값이 100에 가까울수록 활동이 적어 하위 그룹에 속함</li>
	 * </ul>
	 *
	 * @param commits     커밋 수
	 * @param commitLines 커밋 라인 수
	 * @param stars       스타 수
	 * @param followers   팔로워 수
	 * @param duration    대상 기간 ({@link RankingPeriod})
	 * @return 백분위 값 (0=최상위, 100=최하위)
	 */
	private double calculatePercentile(int commits, int commitLines, int stars, int followers, RankingPeriod duration) {

		double commitsMedian = switch (duration) {
			case YEAR -> YEARLY_COMMITS_MEDIAN;
			case MONTH -> MONTHLY_COMMITS_MEDIAN;
			case WEEK -> WEEKLY_COMMITS_MEDIAN;
		};

		double commitLinesMedian = switch (duration) {
			case YEAR -> YEARLY_COMMITS_LINES_MEDIAN;
			case MONTH -> MONTHLY_COMMITS_LINES_MEDIAN;
			case WEEK -> WEEKLY_COMMITS_LINES_MEDIAN;
		};

		double rank = 1 - (COMMITS_WEIGHT * exponentialCdf(commits / commitsMedian) +
			COMMITS_LINES_WEIGHT * logNormalCdf(commitLines / commitLinesMedian) +
			STARS_WEIGHT * exponentialCdf(stars / STARS_MEDIAN) +
			FOLLOWERS_WEIGHT * exponentialCdf(followers / FOLLOWERS_MEDIAN)) / TOTAL_WEIGHT;

		return rank * 100;
	}

	/**
	 * 지수 분포 기반 CDF (Cumulative Distribution Function).
	 *
	 * <p>공식: {@code 1 - 2^(-x)}</p>
	 *
	 * <p>특징:</p>
	 * <ul>
	 *   <li>작은 입력값에 민감하게 반응 → 초기 활동량을 빠르게 점수화</li>
	 *   <li>x가 커질수록 점증하지만 1에 수렴 → 과도한 활동량의 영향은 제한</li>
	 *   <li>커밋 수, 스타 수, 팔로워 수와 같은 "소수의 증가가 의미 있는 지표"에 적합</li>
	 * </ul>
	 *
	 * @param x 정규화된 입력 값 (예: commits / YEARLY_COMMITS_MEDIAN)
	 * @return 0 ~ 1 범위의 CDF 값
	 */
	private double exponentialCdf(double x) {
		return 1 - Math.pow(2, -x);
	}

	/**
	 * 로그 정규화 CDF (Logistic-like Function).
	 *
	 * <p>공식: {@code x / (1 + x)}</p>
	 *
	 * <p>특징:</p>
	 * <ul>
	 *   <li>입력이 커질수록 1에 점근 → 큰 값일수록 영향력이 제한됨</li>
	 *   <li>중간 구간에서 점진적으로 증가 → 분포가 큰 지표에서도 균형 있게 점수화</li>
	 *   <li>커밋 라인 수와 같이 값의 편차가 큰 지표에 적합</li>
	 * </ul>
	 *
	 * @param x 정규화된 입력 값 (예: commitLines / YEARLY_COMMITS_LINES_MEDIAN)
	 * @return 0 ~ 1 범위의 CDF 값
	 */
	private double logNormalCdf(double x) {
		return x / (1 + x);
	}
}
