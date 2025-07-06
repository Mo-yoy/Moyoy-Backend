package com.moyo.backend.domain.pr_review.business;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyo.backend.domain.pr_review.business.dto.PrReviewListResult;
import com.moyo.backend.domain.pr_review.business.dto.PrReviewSearchCriteria;
import com.moyo.backend.domain.pr_review.implement.PrReviewHitsReader;
import com.moyo.backend.domain.pr_review.implement.PrReviewReader;
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
		PrReviewListData result = prReviewReader.readListByCriteria(criteria.status(), criteria.position(), criteria.order(), criteria.page(), criteria.size());

		// 2. implement 계층 응답 dto -> business 계층 dto로 변환 후, 반환.
		return new PrReviewListResult(result.prReviews(), result.isLast());
	}

	public PrReviewListResult getMyPrReviewList(Long userId, PrReviewSearchCriteria criteria) {

		// 1. userId가 작성한 요청글들을 글 상태, 직무 태그 순으로 정렬된 리스트 얻기.
		PrReviewListData result = prReviewReader.readMyListByCriteria(userId, criteria.status(), criteria.position(), criteria.order(), criteria.page(), criteria.size());

		// 2. implement 계층 응답 dto -> business 계층 dto 변환 후, 반환.
		return new PrReviewListResult(result.prReviews(), result.isLast());
	}
}
