package com.moyoy.domain.pr_review;

import java.util.List;

public interface PrReviewHitCountRepository {

	/// 조회수 증가분을 누적
	void increase(Long prReviewId);

	/// 누적된 증가분을 모두 꺼내고 비움
	List<PrReviewHitCount> popAll();

	/// RDB 반영에 실패한 증가분을 되돌려 놓음
	void restore(PrReviewHitCount hitCount);
}
