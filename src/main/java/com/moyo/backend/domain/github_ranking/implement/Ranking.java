package com.moyo.backend.domain.github_ranking.implement;

import com.moyo.backend.batch.ranking.processor.RankingCalculatorResult;
import com.moyo.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "rankings")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Ranking extends BaseTimeEntity {

	@Id
	@Column(name = "ranking_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;
	private String grade;
	private long weeklyPoint;
	private long monthlyPoint;
	private long yearlyPoint;

	public void updateRankingByBatch(RankingCalculatorResult rankingCalculatorResult) {

		this.grade = rankingCalculatorResult.rankingGrade();
		this.weeklyPoint = rankingCalculatorResult.weekRankingPoint();
		this.monthlyPoint = rankingCalculatorResult.monthRankingPoint();
		this.yearlyPoint = rankingCalculatorResult.yearRankingPoint();
	}
}
