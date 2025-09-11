package com.moyoy.domain.ranking;

import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.dto.RankingCalculatorParameters;
import com.moyoy.domain.ranking.dto.RankingCalculatorResult;

/**
 * TODO: 기간(주/월/년)별 랭킹 산정 로직 분기 필요
 *
 * <p><b>AS-IS</b>:
 * <ul>
 *   <li>모든 기간(WEEK, MONTH, YEAR)에 대해 동일한 지표와 산식 사용</li>
 *   <li>Grade(등급) 또한 기간 구분 없이 동일한 기준으로 산출</li>
 * </ul>
 *
 * <p><b>TO-BE</b>:
 * <ul>
 *   <li>기간별로 다른 지표 또는 가중치 적용 (예: 주간은 단기 성과, 연간은 누적 성과 반영)</li>
 *   <li>Grade(등급)는 <b>연간(YEAR)</b> 기준으로만 산출</li>
 *   <li>주간/월간은 점수(포인트)만 산출하고, 등급은 부여하지 않음</li>
 * </ul>
 *
 * <p><b>비고</b>:
 * - 현재는 모든 기간의 랭킹이 동일하게 계산되므로,
 *   단기 성과(주간)와 장기 성과(연간)의 차이를 반영하지 못함.
 * - 분기 처리가 완료되면 등급은 "연간 성과"만 대표하도록 변경 예정.
 * </p>
 */

@Component
public class RankingCalculator {

	/**
	 * <H1>랭킹 산정 기준 상수</H1>
	 *
	 * <p>GitHub 활동 지표(커밋, 커밋 라인 수, 스타, 팔로워)를 기반으로
	 * 백분위와 등급을 계산하기 위한 기준값들입니다.</p>
	 * 각 평가 항목의 중앙값(Median)과 가중치(Weight).
	 *
	 * <ul>
	 *   <li><b>Median</b>: 분포의 중심값 (50% 사용자가 이 값보다 낮고, 50%는 높음)</li>
	 *   <li><b>Weight</b>: 전체 점수 산정 시 해당 항목의 비중</li>
	 * </ul>
	 *
	 * <p>현행</p>
	 * <ul>
	 *   <li>커밋 수와 커밋 라인 수는 기여도의 핵심 지표이므로 높은 가중치 부여</li>
	 *   <li>스타 수와 팔로워 수는 활동의 "외부 인지도"를 반영하되 영향력은 낮음</li>
	 * </ul>
	 */
	private static final double COMMITS_MEDIAN = 100.0, COMMITS_WEIGHT = 3.0;
	private static final double COMMITS_LINES_MEDIAN = 5000.0, COMMITS_LINES_WEIGHT = 5.0;
	private static final double STARS_MEDIAN = 10.0, STARS_WEIGHT = 1.0;
	private static final double FOLLOWERS_MEDIAN = 30.0, FOLLOWERS_WEIGHT = 1.0;

	/// 전체 가중치 총합 (10.0)
	private static final double TOTAL_WEIGHT = COMMITS_WEIGHT + COMMITS_LINES_WEIGHT + STARS_WEIGHT + FOLLOWERS_WEIGHT;

	/**
	 * 누적 백분위(Percentile) 기준값 배열.
	 *
	 * <p>계산된 퍼센타일 값이 이 배열의 값 이하일 경우,
	 * 동일 인덱스의 {@link #LEVELS} 등급을 부여합니다.</p>
	 *
	 * <p>예시: 퍼센타일이 20이라면 {@code THRESHOLDS[2] = 25} 이하이므로
	 * {@code LEVELS[2] = "A"} 등급을 부여.</p>
	 *
	 * <p>퍼센타일이 낮을수록 더 높은 성과를 의미합니다.</p>
	 */
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
	 * 특정 사용자의 GitHub 활동 지표를 기반으로 최종 랭킹 등급을 산출합니다.
	 *
	 * <p>계산 절차:</p>
	 * <ol>
	 *   <li>{@link #calculatePercentile(int, int, int, int)} 로 백분위(0~100)를 계산</li>
	 *   <li>백분위 값이 {@link #THRESHOLDS} 배열의 어느 구간에 속하는지 판별</li>
	 *   <li>해당 구간의 {@link #LEVELS} 값을 반환</li>
	 * </ol>
	 *
	 * <p>랭킹 산정 기준:</p>
	 * <ul>
	 *   <li>커밋 개수 (사용자/조직의 실제 기여 커밋)</li>
	 *   <li>커밋 라인 수 (커밋 개수보다 더 중요한 요소)</li>
	 *   <li>스타 수 (상대적으로 낮은 가중치)</li>
	 *   <li>팔로워 수 (상대적으로 낮은 가중치)</li>
	 * </ul>
	 *
	 * @param commits    총 커밋 수
	 * @param commitLines 총 커밋 코드 라인 수
	 * @param stars      총 star 개수
	 * @param followers  사용자의 팔로워 수
	 * @return           등급 문자열 ("S" ~ "C")
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

	/**
	 * 특정 사용자의 활동 지표를 기반으로 랭킹 점수(포인트)를 계산합니다.
	 *
	 * <p>계산 절차:</p>
	 * <ol>
	 *   <li>{@link #calculatePercentile(int, int, int, int)} 로 백분위(0~100)를 계산</li>
	 *   <li>백분위를 역전(100 - percentile)하여 점수화
	 *       <br/> → 활동이 많을수록 percentile ↓, 점수 ↑</li>
	 *   <li>기본 배점(100,000)과 기간 가중치({@link RankingPeriod#getWeight()})를 곱해 최종 점수를 산출</li>
	 * </ol>
	 *
	 * <p>예시:</p>
	 * <ul>
	 *   <li>percentile = 20, duration.weight = 4 (월간)
	 *       → (100 - 20) * 100,000 * 4 = 32,000,000 점</li>
	 *   <li>percentile = 80, duration.weight = 1 (주간)
	 *       → (100 - 80) * 100,000 * 1 = 2,000,000 점</li>
	 * </ul>
	 *
	 * @param commits     총 커밋 수
	 * @param commitLines 총 커밋 라인 수
	 * @param stars       총 star 개수
	 * @param followers   팔로워 수
	 * @param duration    점수를 산정할 기간 (주/월/년) 가중치
	 * @return            산출된 랭킹 점수
	 */
	private long calculateRankingPoint(int commits, int commitLines, int stars, int followers, RankingPeriod duration) {

		double percentile = calculatePercentile(commits, commitLines, stars, followers);

		return (long)((100 - percentile) * 100_000 * duration.getWeight());
	}

	/**
	 * 사용자의 활동 지표를 기반으로 백분위(0 ~ 100)를 계산합니다.
	 *
	 * <p>계산 방식:</p>
	 * <ol>
	 *   <li>각 항목(커밋 수, 커밋 라인 수, 스타, 팔로워)을 정규화
	 *       <ul>
	 *         <li>커밋 수, 스타, 팔로워 → {@link #exponentialCdf(double)} 로 변환</li>
	 *         <li>커밋 라인 수 → {@link #logNormalCdf(double)} 로 변환</li>
	 *       </ul>
	 *   </li>
	 *   <li>각 항목의 점수에 항목별 가중치({@code COMMITS_WEIGHT}, {@code COMMITS_LINES_WEIGHT}, …)를 곱함</li>
	 *   <li>전체 가중치({@code TOTAL_WEIGHT})로 나누어 평균을 구함</li>
	 *   <li>1에서 빼서 "상대 점수(rank)"로 변환 (활동 ↑ → rank ↑ → percentile ↓)</li>
	 *   <li>rank를 0~100 범위의 백분위로 변환하여 반환</li>
	 * </ol>
	 *
	 * <p>특징:</p>
	 * <ul>
	 *   <li>활동량이 많을수록 percentile 값은 낮아짐 (즉, 상위 랭킹에 가까움)</li>
	 *   <li>활동이 거의 없으면 percentile 값이 100에 가까워져 최하위 등급에 매핑됨</li>
	 * </ul>
	 *
	 * @param commits     총 커밋 수
	 * @param commitLines 총 커밋 라인 수
	 * @param stars       스타 수
	 * @param followers   팔로워 수
	 * @return            계산된 백분위 (0 = 최고 성과, 100 = 최하위 성과)
	 */
	private double calculatePercentile(int commits, int commitLines, int stars, int followers) {

		// 각 항목의 점수를 계산한 후 전체 가중치로 평균 → 1에서 빼면 "상대 점수 Percentile"
		double rank = 1 - (COMMITS_WEIGHT * exponentialCdf(commits / COMMITS_MEDIAN) +
			COMMITS_LINES_WEIGHT * logNormalCdf(commitLines / COMMITS_LINES_MEDIAN) +
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
	 *   <li>작은 값의 변화에 민감하게 반응 → 초기 활동량에 빠르게 점수 상승</li>
	 *   <li>큰 값으로 갈수록 점수 증가폭이 점차 둔화 → 과도한 활동량의 영향력 억제</li>
	 *   <li>활동 초반의 “소수 정예”를 부각시키고 싶을 때 적합</li>
	 * </ul>
	 *
	 * @param x 정규화된 입력 값 (예: commits / COMMITS_MEDIAN)
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
	 *   <li>입력이 커질수록 1에 점근 → 극단적인 큰 값의 영향력을 제한</li>
	 *   <li>중간 구간에서 점수가 점진적으로 증가하여 균형 있는 분포 제공</li>
	 *   <li>커밋 라인 수처럼 값의 편차가 클 때 유용</li>
	 * </ul>
	 *
	 * @param x 정규화된 입력 값 (예: commitLines / COMMITS_LINES_MEDIAN)
	 * @return 0 ~ 1 범위의 CDF 값
	 */
	private double logNormalCdf(double x) {
		return x / (1 + x);
	}
}
