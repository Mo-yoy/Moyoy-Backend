package com.moyo.backend.domain.pr_review.business;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moyo.backend.common.exception.CommonErrorCode;
import com.moyo.backend.common.exception.MoyoException;
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

		// 1. 글 상태, 직무 태그 순으로 정렬된 리스트 얻기.
		PrReviewListData data = prReviewReader.readListByCriteria(criteria.status(), criteria.position(), criteria.order(), criteria.page(), criteria.size());

		// 2. implement 계층 응답 dto -> business 계층 dto로 변환 후, 반환.
		return new PrReviewListResult(data.prReviews(), data.isLast());
	}

	public PrReviewListResult getMyPrReviewList(Long userId, PrReviewSearchCriteria criteria) {

		// 1. userId가 작성한 요청글들을 글 상태, 직무 태그 순으로 정렬된 리스트 얻기.
		PrReviewListData data = prReviewReader.readMyListByCriteria(userId, criteria.status(), criteria.position(), criteria.order(), criteria.page(), criteria.size());

		// 2. implement 계층 응답 dto -> business 계층 dto 변환 후, 반환.
		return new PrReviewListResult(data.prReviews(), data.isLast());
	}

	public PrReviewDetailResult getPrReviewDetail(Long reviewId, Long userId) {

		// 1. 요청글 상세정보 얻기.
		PrReviewDetail data = prReviewReader.readPrReviewDetail(reviewId, userId);

		// 2. 조회수 증가 관리 (IP 기반으로 중복 방지 공부 후 적용)
		// TODO

		// 3. implement 계층 응답 dto -> business 계층 dto 변환 후, 반환.
		return PrReviewDetailResult.from(data);
	}

	public PrReviewCreateResult createPrReview(PrReviewContent content, Long userId) {

		// 1. 작성자 정보 불러오기.
		User writer = userReader.findById(userId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

		// 2. 제목, 직군 태그, PR URL, 내용 + 작성자로 요청글 생성.
		PrReview prReview = PrReview.from(content.title(), content.position(), content.prUrl(), content.content(), writer);

		// 2. 요청글 저장 후, 요청글 ID 반환.
		PrReviewCreateData data = prReviewUpdater.create(prReview);

		// 2. implement 계층 응답 dto -> business 계층 dto 변환 후, 반환.
		return new PrReviewCreateResult(data.prReviewId());
	}

	public PrReviewContent getPrReviewUpdateForm(Long reviewId, Long userId) {

		// 1. 기존에 작성한 요청글 내용 불러오기.
		PrReviewContentData data = prReviewReader.readPrReviewContent(reviewId, userId);

		// 2. 요청한 사용자가 작성자가 아니라면 예외 처리.
		if (!data.isWriter()) {
			throw new MoyoException(CommonErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
		}

		// 3. implement 계층 dto -> business 계층 dto로 변환 후, 반환.
		return PrReviewContent.from(data);
	}

	@Transactional
	public PrReviewUpdateResult updatePrReview(Long reviewId, PrReviewContent content, Long userId) {

		// 1. 수정할 기존의 요청글 가져오기.
		PrReview prReview = prReviewReader.readPrReview(reviewId);

		// 2. 요청한 사용자가 작성자가 아니라면 예외 처리.
		if (!prReview.getUser().getId().equals(userId)) {
			throw new MoyoException(CommonErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
		}

		// 3. 변경 내용 반영.
		prReview.updateDetail(content.title(), content.position(), content.prUrl(), content.content());

		// 4. implement 계층 응답 -> business 계층 dto 변환 후, 반환.
		return new PrReviewUpdateResult(prReview.getId());
	}

	@Transactional
	public void deletePrReview(Long reviewId, Long userId) {

		// 1. 삭제할 기존의 요청글 가져오기 (유효한 reviewId인지 검증도 포함)
		PrReview prReview = prReviewReader.readPrReview(reviewId);

		// 2. 작성자인지 확인.
		if (!prReview.getUser().getId().equals(userId)) {
			throw new MoyoException(CommonErrorCode.PR_REVIEW_DELETE_FORBIDDEN);
		}

		// 3. 조회수 테이블 따로 둘 경우, 해당 요청글의 조회수 데이터 삭제 (아직 방식 고민 중)
		// TODO

		// 4. 요청글 삭제.
		prReviewUpdater.delete(prReview);
	}
}
