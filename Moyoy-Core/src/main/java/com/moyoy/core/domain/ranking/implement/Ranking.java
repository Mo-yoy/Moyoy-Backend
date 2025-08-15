package com.moyoy.core.domain.ranking.implement;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Ranking{

	private Long id;
	private Long userId;
	private String grade;
	private long weeklyPoint;
	private long monthlyPoint;
	private long yearlyPoint;


	public void updateRankingByBatch(RankingUpdate rankingUpdate) {

		this.grade = rankingUpdate.grade();
		this.weeklyPoint = rankingUpdate.weeklyPoint();
		this.monthlyPoint = rankingUpdate.monthlyPoint();
		this.yearlyPoint = rankingUpdate.yearlyPoint();
	}

	public static Ranking initRanking(Long userId) {
		return Ranking.builder()
			.userId(userId)
			.grade("C")
			.weeklyPoint(0L)
			.monthlyPoint(0L)
			.yearlyPoint(0L)
			.build();
	}
}
