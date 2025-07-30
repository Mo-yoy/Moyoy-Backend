package com.moyo.backend.domain.pr_review.business;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moyo.backend.common.exception.MoyoException;
import com.moyo.backend.common.exception.pr_review.PrReviewErrorCode;
import com.moyo.backend.common.exception.user.UserErrorCode;
import com.moyo.backend.domain.pr_review.business.dto.*;
import com.moyo.backend.domain.pr_review.business.dto.PrReviewContent;
import com.moyo.backend.domain.pr_review.implement.PrReview;
import com.moyo.backend.domain.pr_review.implement.PrReviewHitsReader;
import com.moyo.backend.domain.pr_review.implement.PrReviewReader;
import com.moyo.backend.domain.pr_review.implement.PrReviewUpdater;
import com.moyo.backend.domain.pr_review.implement.dto.*;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserReader;

@Service
@RequiredArgsConstructor
public class PrReviewService {

	private final PrReviewReader prReviewReader;
	private final UserReader userReader;
	private final PrReviewHitsReader prReviewHitsReader;
	private final PrReviewUpdater prReviewUpdater;

	public PrReviewListResult getPrReviewList(PrReviewSearchCriteria criteria) {

		PrReviewListData data = prReviewReader.readListByCriteria(criteria.status(), criteria.order(), criteria.position(), criteria.page(), criteria.size());

		return new PrReviewListResult(data.prReviews(), data.isLast());
	}

	public PrReviewListResult getMyPrReviewList(Long userId, PrReviewSearchCriteria criteria) {

		PrReviewListData data = prReviewReader.readMyListByCriteria(userId, criteria.status(), criteria.order(), criteria.position(), criteria.page(), criteria.size());

		return new PrReviewListResult(data.prReviews(), data.isLast());
	}

	public PrReviewDetailResult getPrReviewDetail(Long reviewId, Long userId) {
		PrReviewDetail data = prReviewReader.readPrReviewDetail(reviewId, userId);

		// 2. 조회수 증가 관리 (IP 기반으로 중복 방지 공부 후 적용)
		// TODO

		return PrReviewDetailResult.from(data);
	}

	public PrReviewCreateResult createPrReview(PrReviewContent content, Long userId) {

		User writer = userReader.findById(userId)
			.orElseThrow(() -> new MoyoException(UserErrorCode.USER_NOT_FOUND));

		PrReview prReview = PrReview.from(content.title(), content.position(), content.prUrl(), content.content(), writer);

		PrReviewCreateData data = prReviewUpdater.create(prReview);

		return new PrReviewCreateResult(data.prReviewId());
	}

	public PrReviewContent getPrReviewUpdateForm(Long reviewId, Long userId) {

		PrReviewContentData data = prReviewReader.readPrReviewContent(reviewId, userId);

		if (!data.isWriter()) {
			throw new MoyoException(PrReviewErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
		}

		return PrReviewContent.from(data);
	}

	@Transactional
	public PrReviewUpdateResult updatePrReview(Long reviewId, PrReviewContent content, Long userId) {

		PrReview prReview = prReviewReader.readPrReview(reviewId);

		if (!prReview.getUser().getId().equals(userId)) {
			throw new MoyoException(PrReviewErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
		}

		prReview.updateDetail(content.title(), content.position(), content.prUrl(), content.content());

		return new PrReviewUpdateResult(prReview.getId());
	}

	@Transactional
	public void deletePrReview(Long reviewId, Long userId) {

		PrReview prReview = prReviewReader.readPrReview(reviewId);

		if (!prReview.getUser().getId().equals(userId)) {
			throw new MoyoException(PrReviewErrorCode.PR_REVIEW_DELETE_FORBIDDEN);
		}

		// 3. 조회수 테이블 따로 둘 경우, 해당 요청글의 조회수 데이터 삭제 (아직 방식 고민 중)
		// TODO

		prReviewUpdater.delete(prReview);
	}
}
