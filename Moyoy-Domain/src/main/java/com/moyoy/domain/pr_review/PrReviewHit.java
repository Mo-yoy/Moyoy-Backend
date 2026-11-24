package com.moyoy.domain.pr_review;

import java.time.Duration;

import lombok.Builder;
import lombok.Getter;

import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

@Getter
@Builder
public class PrReviewHit {
	private Long prReviewId;
	private String viewerKey;

	/// 같은 조회자가 이 기간 안에 다시 조회하면 조회수를 증가시키지 않음
	public static final Duration DUPLICATE_WINDOW = Duration.ofHours(1);

	/// 로그인 사용자는 회원 ID, 익명 사용자는 IP로 조회자를 식별
	public static PrReviewHit create(PrReviewHitCreate dto) {
		String viewerKey = dto.userId() != null
			? "user::" + dto.userId()
			: "ip::" + dto.clientIp();

		return new PrReviewHit(dto.prReviewId(), viewerKey);
	}
}
