package com.moyoy.domain.pr_review;

import java.util.List;

/// RDB 반영에 실패해 다음 반영 때 다시 시도할 조회수 증가분 보관
public interface PrReviewHitCountRepository {

	/// 보관 중인 증가분을 모두 꺼내고 비움
	List<PrReviewHitCount> popAll();

	/// RDB 반영에 실패한 증가분을 보관
	void restore(PrReviewHitCount hitCount);
}
