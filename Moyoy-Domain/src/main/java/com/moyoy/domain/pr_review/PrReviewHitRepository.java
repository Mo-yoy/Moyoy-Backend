package com.moyoy.domain.pr_review;

public interface PrReviewHitRepository {

	/// 중복 판단 기간 내 첫 조회일 때만 기록하고 true 반환
	boolean saveIfAbsent(PrReviewHit prReviewHit);
}
