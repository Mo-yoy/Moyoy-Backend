package com.moyoy.domain.pr_review;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.Builder;
import lombok.Getter;

import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

@Getter
@Builder
public class PrReviewHit {
	private Long prReviewId;
	private String viewerKey;
	private LocalDateTime viewedAt;

	/// 같은 조회자는 1시간 구간마다 최대 한 번만 조회수에 반영
	public static final Duration WINDOW = Duration.ofHours(1);

	/// 로그인 사용자는 회원 ID, 익명 사용자는 IP로 조회자를 식별
	public static PrReviewHit create(PrReviewHitCreate dto) {
		String viewerKey = dto.userId() != null
			? "user::" + dto.userId()
			: "ip::" + dto.clientIp();

		return new PrReviewHit(dto.prReviewId(), viewerKey, dto.viewedAt());
	}

	/// 조회가 속한 1시간 구간의 시작 시각
	public LocalDateTime getWindowStart() {
		return viewedAt.truncatedTo(ChronoUnit.HOURS);
	}
}
