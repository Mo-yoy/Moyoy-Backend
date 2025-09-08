package com.moyoy.domain.ranking;

import lombok.Builder;
import lombok.Getter;

import com.moyoy.domain.ranking.dto.RankingUpdate;

@Getter
@Builder
public class Ranking {

	private Long id;
	private Long userId;
	private String grade;
	private long weeklyPoint;
	private long monthlyPoint;
	private long yearlyPoint;

	public void update(RankingUpdate rankingUpdate) {

		this.grade = rankingUpdate.grade();
		this.weeklyPoint = rankingUpdate.weeklyPoint();
		this.monthlyPoint = rankingUpdate.monthlyPoint();
		this.yearlyPoint = rankingUpdate.yearlyPoint();
	}

	public static Ranking createInitial(Long userId) {
		return Ranking.builder()
			.userId(userId)
			.grade("C")
			.weeklyPoint(0L)
			.monthlyPoint(0L)
			.yearlyPoint(0L)
			.build();
	}
}
