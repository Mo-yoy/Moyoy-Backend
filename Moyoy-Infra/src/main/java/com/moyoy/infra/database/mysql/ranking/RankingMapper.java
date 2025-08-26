package com.moyoy.infra.database.mysql.ranking;

import com.moyoy.domain.ranking.Ranking;

public class RankingMapper {

	public static Ranking toModel(RankingEntity rankingEntity) {

		return Ranking.builder()
			.id(rankingEntity.getId())
			.userId(rankingEntity.getUserId())
			.grade(rankingEntity.getGrade())
			.weeklyPoint(rankingEntity.getWeeklyPoint())
			.monthlyPoint(rankingEntity.getMonthlyPoint())
			.yearlyPoint(rankingEntity.getYearlyPoint())
			.build();
	}

	public static RankingEntity toEntity(Ranking ranking) {

		return RankingEntity.builder()
			.id(ranking.getId())
			.userId(ranking.getUserId())
			.grade(ranking.getGrade())
			.weeklyPoint(ranking.getWeeklyPoint())
			.monthlyPoint(ranking.getMonthlyPoint())
			.yearlyPoint(ranking.getYearlyPoint())
			.build();
	}
}
