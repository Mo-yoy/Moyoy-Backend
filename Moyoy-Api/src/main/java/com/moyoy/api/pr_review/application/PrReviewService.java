package com.moyoy.api.pr_review.application;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moyoy.api.pr_review.application.request.PrReviewCreateData;
import com.moyoy.api.pr_review.application.request.PrReviewUpdateData;
import com.moyoy.api.pr_review.application.request.SearchConditionData;
import com.moyoy.api.pr_review.application.response.*;

import com.moyoy.domain.pr_review.PrReview;
import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.PrReviewHitRepository;
import com.moyoy.domain.pr_review.PrReviewRepository;
import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;
import com.moyoy.domain.pr_review.error.PrReviewDeleteForbiddenException;
import com.moyoy.domain.pr_review.error.PrReviewEditForbiddenException;
import com.moyoy.domain.pr_review.error.PrReviewNotFoundException;

import com.moyoy.infra.database.mysql.pr_review.PrReviewQueryRepository;
import com.moyoy.infra.database.mysql.pr_review.response.PrReviewDetailData;
import com.moyoy.infra.database.mysql.pr_review.response.PrReviewSummaryData;

import com.moyoy.common.page.SliceResult;

@Service
@RequiredArgsConstructor
public class PrReviewService {

	private final PrReviewService self;
	private final PrReviewRepository prReviewRepository;
	private final PrReviewQueryRepository prReviewQueryRepository;
	private final PrReviewHitRepository prReviewHitRepository;

	public PrReviewListResult getPrReviewList(SearchConditionData condition) {

		SliceResult<PrReviewSummaryData> slice = prReviewQueryRepository.findAll(condition.toQueryCondition());

		return PrReviewListResult.from(slice);
	}

	public PrReviewDetailResult getPrReviewDetail(Long reviewId, Long userId) {

		PrReviewDetailData data = prReviewQueryRepository.findById(reviewId) /// TODO querydsl 데이터 반환
			.orElseThrow(PrReviewNotFoundException::new);

		boolean isWriter = data.userId().equals(userId);

		/// TODO: 조회수 관리 (v1: 브릿지 테이블, v2: Redis, v3: Redis 기록/누적조회 일괄 업데이트, v4: hyperloglog/누적조회 일괄 업데이트)
		///  특히 조회수 증가는 도메인 서비스인데, 뷰목적 dto 뿐아니라 entity로 도메인도 가져와야함.
		self.increaseHitAsync(reviewId, userId);

		return PrReviewDetailResult.from(data, isWriter);
	}

	public PrReviewCreateResult createPrReview(PrReviewCreateData data, Long userId) {

		PrReview newPrReview = PrReview.create(data.toCommand(userId));

		Long createdReviewId = prReviewRepository.save(newPrReview).getId();

		return PrReviewCreateResult.from(createdReviewId);
	}

	public PrReviewUpdateResult updatePrReview(Long reviewId, PrReviewUpdateData data, Long userId) {

		PrReview prReview = prReviewRepository.findById(reviewId)
			.orElseThrow(PrReviewNotFoundException::new);

		if (!prReview.getUserId().equals(userId)) {
			throw new PrReviewEditForbiddenException();
		}

		prReview.updateDetail(data.toCommand());

		Long updatedReviewId = prReviewRepository.save(prReview).getId();

		return new PrReviewUpdateResult(updatedReviewId);
	}

	public void deletePrReview(Long reviewId, Long userId) {

		PrReview prReview = prReviewRepository.findById(reviewId)
			.orElseThrow(PrReviewNotFoundException::new);

		if (!prReview.getUserId().equals(userId)) {
			throw new PrReviewDeleteForbiddenException();
		}

		if (prReview.getStatus().isClosed()) {
			throw new PrReviewDeleteForbiddenException();
		}

		prReviewRepository.deleteById(reviewId);
	}

	public PrReviewCloseResult closePrReview(Long reviewId, Long userId, LocalDateTime eventTime) {

		PrReview prReview = prReviewRepository.findById(reviewId)
			.orElseThrow(PrReviewNotFoundException::new);

		if (!prReview.getUserId().equals(userId)) {
			throw new PrReviewEditForbiddenException();
		}

		prReview.close(eventTime);

		Long closedReviewId = prReviewRepository.save(prReview).getId();

		return new PrReviewCloseResult(closedReviewId);
	}

	@Async("hitsExecutor")
	@Transactional
	public void increaseHitAsync(Long reviewId, Long userId) {
		/// 해당 트랜잭션이 실패하면, 후처리를 할 것인가.

		LocalDateTime now = LocalDateTime.now();

		PrReviewHit hit = prReviewHitRepository.findOrCreate(
			PrReviewHit.create(new PrReviewHitCreate(reviewId, userId, now)));

		if (!hit.canIncrease(now))
			return;

		prReviewRepository.increaseHitCount(reviewId);

		prReviewHitRepository.updateLastIncreasedAt(hit.getId(), now);
	}
}
