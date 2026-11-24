package com.moyoy.batch.scheduler;

import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moyoy.domain.pr_review.PrReviewHitCount;
import com.moyoy.domain.pr_review.PrReviewHitCountRepository;
import com.moyoy.domain.pr_review.PrReviewHitRepository;
import com.moyoy.domain.pr_review.PrReviewRepository;

@ExtendWith(MockitoExtension.class)
class PrReviewHitCountFlushSchedulerTest {

	@Mock
	private PrReviewHitRepository prReviewHitRepository;

	@Mock
	private PrReviewHitCountRepository prReviewHitCountRepository;

	@Mock
	private PrReviewRepository prReviewRepository;

	@InjectMocks
	private PrReviewHitCountFlushScheduler scheduler;

	@Test
	void 종료된_구간의_고유_조회자_수와_이전에_실패한_증가분을_RDB에_반영한다() {

		// given
		PrReviewHitCount closedWindow = hitCount(1L, 3);
		PrReviewHitCount previouslyFailed = hitCount(2L, 5);
		given(prReviewHitRepository.popClosedWindowCounts(any())).willReturn(List.of(closedWindow));
		given(prReviewHitCountRepository.popAll()).willReturn(List.of(previouslyFailed));

		// when
		scheduler.flush();

		// then
		then(prReviewRepository).should().increaseHitCount(1L, 3);
		then(prReviewRepository).should().increaseHitCount(2L, 5);
		then(prReviewHitCountRepository).should(never()).restore(any());
	}

	@Test
	void 반영에_실패한_증가분은_보관하고_나머지는_계속_반영한다() {

		// given
		PrReviewHitCount failed = hitCount(1L, 3);
		PrReviewHitCount succeeded = hitCount(2L, 5);
		given(prReviewHitRepository.popClosedWindowCounts(any())).willReturn(List.of(failed, succeeded));
		given(prReviewHitCountRepository.popAll()).willReturn(List.of());
		willThrow(new RuntimeException("DB 장애")).given(prReviewRepository).increaseHitCount(1L, 3);

		// when
		scheduler.flush();

		// then
		then(prReviewHitCountRepository).should().restore(failed);
		then(prReviewRepository).should().increaseHitCount(2L, 5);
	}

	private PrReviewHitCount hitCount(Long prReviewId, int count) {
		return PrReviewHitCount.builder().prReviewId(prReviewId).count(count).build();
	}
}
