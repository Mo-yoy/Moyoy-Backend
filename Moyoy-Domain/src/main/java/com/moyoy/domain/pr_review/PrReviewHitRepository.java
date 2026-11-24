package com.moyoy.domain.pr_review;

import java.time.LocalDateTime;
import java.util.List;

public interface PrReviewHitRepository {

	/// 조회가 속한 1시간 구간에 조회자를 기록
	void save(PrReviewHit prReviewHit);

	/// 종료된 구간들의 요청글별 고유 조회자 수를 꺼내고 비움
	List<PrReviewHitCount> popClosedWindowCounts(LocalDateTime now);
}
