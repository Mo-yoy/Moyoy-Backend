package com.moyoy.domain.pr_review;

import lombok.Builder;
import lombok.Getter;

/// RDB에 아직 반영되지 않은 요청글별 조회수 증가분
@Getter
@Builder
public class PrReviewHitCount {
	private Long prReviewId;
	private int count;
}
