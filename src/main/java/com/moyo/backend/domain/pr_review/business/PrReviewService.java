package com.moyo.backend.domain.pr_review.business;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyo.backend.domain.pr_review.business.dto.PrReviewDetailResult;
import com.moyo.backend.domain.pr_review.business.dto.PrReviewListResult;
import com.moyo.backend.domain.pr_review.business.dto.PrReviewSearchCriteria;
import com.moyo.backend.domain.pr_review.implement.PrReviewHitsReader;
import com.moyo.backend.domain.pr_review.implement.PrReviewReader;
import com.moyo.backend.domain.pr_review.implement.dto.PrReviewDetail;
import com.moyo.backend.domain.pr_review.implement.dto.PrReviewListData;
import com.moyo.backend.domain.user.implement.UserReader;

@Service
@RequiredArgsConstructor
public class PrReviewService {

	private final PrReviewReader prReviewReader;
	private final UserReader userReader;
	private final PrReviewHitsReader prReviewHitsReader;

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

		// 2. 조회수 관리 (IP 기반으로 중복 방지 공부 후 적용)
		// TODO

		// 3. implement 계층 응답 dto -> business 계층 dto 변환 후, 반환.
		return PrReviewDetailResult.from(data);
	}
}
