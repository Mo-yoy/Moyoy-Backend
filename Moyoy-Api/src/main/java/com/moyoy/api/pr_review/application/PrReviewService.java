package com.moyoy.api.pr_review.application;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.api.pr_review.application.request.PrReviewCreateData;
import com.moyoy.api.pr_review.application.request.PrReviewUpdateData;
import com.moyoy.api.pr_review.application.request.SearchConditionData;
import com.moyoy.api.pr_review.application.response.PrReviewCreateResult;
import com.moyoy.api.pr_review.application.response.PrReviewDetailResult;
import com.moyoy.api.pr_review.application.response.PrReviewListResult;
import com.moyoy.api.pr_review.application.response.PrReviewUpdateResult;

import com.moyoy.domain.pr_review.PrReview;
import com.moyoy.domain.pr_review.PrReviewRepository;
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

	private final PrReviewRepository prReviewRepository;
	private final PrReviewQueryRepository prReviewQueryRepository;

	public PrReviewListResult getPrReviewList(SearchConditionData condition) {

		SliceResult<PrReviewSummaryData> slice = prReviewQueryRepository.findAll(condition.toQueryCondition());

		return PrReviewListResult.from(slice);
	}

	public PrReviewDetailResult getPrReviewDetail(Long reviewId, Long userId) {

		PrReviewDetailData data = prReviewQueryRepository.findById(reviewId) /// TODO querydsl 데이터 반환
			.orElseThrow(PrReviewNotFoundException::new);

		boolean isWriter = data.userId().equals(userId);

		/// TODO 2. 조회수 증가 관리 (중복 증가 방지 조사 후 적용)
		///  특히 조회수 증가는 도메인 서비스인데, 뷰목적 dto 뿐아니라 entity로 도메인도 가져와야함.

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
}
