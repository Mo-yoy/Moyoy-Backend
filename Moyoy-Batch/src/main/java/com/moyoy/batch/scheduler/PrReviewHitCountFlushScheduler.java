package com.moyoy.batch.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moyoy.domain.pr_review.PrReviewHitCount;
import com.moyoy.domain.pr_review.PrReviewHitCountRepository;
import com.moyoy.domain.pr_review.PrReviewHitRepository;
import com.moyoy.domain.pr_review.PrReviewRepository;

/// 종료된 1시간 구간의 요청글별 고유 조회자 수를 주기적으로 RDB에 일괄 반영
@Slf4j
@Component
@RequiredArgsConstructor
public class PrReviewHitCountFlushScheduler {

	private final PrReviewHitRepository prReviewHitRepository;
	private final PrReviewHitCountRepository prReviewHitCountRepository;
	private final PrReviewRepository prReviewRepository;

	@Scheduled(fixedDelay = 60_000)
	public void flush() {

		LocalDateTime now = LocalDateTime.now();
		List<PrReviewHitCount> hitCounts = new ArrayList<>(prReviewHitRepository.popClosedWindowCounts(now));
		hitCounts.addAll(prReviewHitCountRepository.popAll());

		for (PrReviewHitCount hitCount : hitCounts) {
			try {
				prReviewRepository.increaseHitCount(hitCount.getPrReviewId(), hitCount.getCount());
			} catch (Exception e) {
				log.warn("요청글 조회수 반영 실패, 증가분을 보관했다가 다음 반영 때 다시 시도합니다. prReviewId={}, count={}",
					hitCount.getPrReviewId(), hitCount.getCount(), e);
				prReviewHitCountRepository.restore(hitCount);
			}
		}
	}
}
