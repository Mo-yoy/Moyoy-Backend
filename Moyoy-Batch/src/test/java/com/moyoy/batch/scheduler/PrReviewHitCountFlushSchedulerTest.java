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
import com.moyoy.domain.pr_review.PrReviewRepository;

@ExtendWith(MockitoExtension.class)
class PrReviewHitCountFlushSchedulerTest {

	@Mock
	private PrReviewHitCountRepository prReviewHitCountRepository;

	@Mock
	private PrReviewRepository prReviewRepository;

	@InjectMocks
	private PrReviewHitCountFlushScheduler scheduler;

	@Test
	void 누적된_증가분을_요청글별로_RDB에_반영한다() {

		// given
		PrReviewHitCount review1 = PrReviewHitCount.builder().prReviewId(1L).count(3).build();
		PrReviewHitCount review2 = PrReviewHitCount.builder().prReviewId(2L).count(5).build();
		given(prReviewHitCountRepository.popAll()).willReturn(List.of(review1, review2));

		// when
		scheduler.flush();

		// then
		then(prReviewRepository).should().increaseHitCount(1L, 3);
		then(prReviewRepository).should().increaseHitCount(2L, 5);
		then(prReviewHitCountRepository).should(never()).restore(any());
	}

	@Test
	void 반영에_실패한_증가분은_되돌리고_나머지는_계속_반영한다() {

		// given
		PrReviewHitCount failed = PrReviewHitCount.builder().prReviewId(1L).count(3).build();
		PrReviewHitCount succeeded = PrReviewHitCount.builder().prReviewId(2L).count(5).build();
		given(prReviewHitCountRepository.popAll()).willReturn(List.of(failed, succeeded));
		willThrow(new RuntimeException("DB 장애")).given(prReviewRepository).increaseHitCount(1L, 3);

		// when
		scheduler.flush();

		// then
		then(prReviewHitCountRepository).should().restore(failed);
		then(prReviewRepository).should().increaseHitCount(2L, 5);
	}
}
