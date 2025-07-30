package com.moyo.backend.domain.batch.ranking.implement;

import com.moyo.backend.domain.github_ranking.implement.Ranking;

public record RankingBatchTaskResult(
	Long userId,
	Ranking ranking,
	boolean success,
	String errorMessage) {
	public static RankingBatchTaskResult success(Long userId, Ranking ranking) {
		return new RankingBatchTaskResult(userId, ranking, true, null);
	}

	public static RankingBatchTaskResult fail(Long userId, String errorMessage) {
		return new RankingBatchTaskResult(userId, null, false, errorMessage);
	}
}
