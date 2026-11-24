package com.moyoy.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moyoy.domain.pr_review.PrReviewHitCount;
import com.moyoy.domain.pr_review.PrReviewHitCountRepository;
import com.moyoy.domain.pr_review.PrReviewRepository;

/// Redis에 누적된 요청글 조회수 증가분을 주기적으로 RDB에 일괄 반영
@Slf4j
@Component
@RequiredArgsConstructor
public class PrReviewHitCountFlushScheduler {

	private final PrReviewHitCountRepository prReviewHitCountRepository;
	private final PrReviewRepository prReviewRepository;

	@Scheduled(fixedDelay = 60_000)
	public void flush() {

		for (PrReviewHitCount hitCount : prReviewHitCountRepository.popAll()) {
			try {
				prReviewRepository.increaseHitCount(hitCount.getPrReviewId(), hitCount.getCount());
			} catch (Exception e) {
				log.warn("요청글 조회수 반영 실패, 증가분을 되돌립니다. prReviewId={}, count={}",
					hitCount.getPrReviewId(), hitCount.getCount(), e);
				prReviewHitCountRepository.restore(hitCount);
			}
		}
	}
}
