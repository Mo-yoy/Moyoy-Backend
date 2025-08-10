package com.moyoy.batch.ranking.job;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.domain.ranking.implement.Ranking;

@Getter
@AllArgsConstructor
public class RankingBatchResult {

	private Long userId;
	private Ranking ranking;
	private boolean success;
	private String errorMessage;

	public static RankingBatchResult init(Long userId) {
		return new RankingBatchResult(userId, null, false, "랭킹 업데이트 실패");
	}

	public void updateOnSuccess(Ranking ranking) {
		this.ranking = ranking;
		this.success = true;
		this.errorMessage = null;
	}
}
