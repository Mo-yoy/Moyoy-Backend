package com.moyoy.api.pr_review.application;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.PrReviewHitRepository;
import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

@Service
@RequiredArgsConstructor
public class PrReviewAsyncService {

	private final PrReviewHitRepository prReviewHitRepository;

	/// 1시간 구간별로 조회자를 기록하고, 구간이 끝나면 Batch에서 고유 조회자 수를 RDB에 일괄 반영
	@Async("hitsExecutor")
	public void increaseHitAsync(Long reviewId, Long userId, String clientIp) {

		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(reviewId, userId, clientIp, LocalDateTime.now()));

		prReviewHitRepository.save(hit);
	}
}
