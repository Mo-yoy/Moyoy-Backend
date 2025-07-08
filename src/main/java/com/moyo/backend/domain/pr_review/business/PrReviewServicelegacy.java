//package com.moyo.backend.domain.pr_review.business;
//
//import com.moyo.backend.common.exception.CommonErrorCode;
//import com.moyo.backend.common.exception.MoyoException;
//import com.moyo.backend.domain.pr_review.implement.PrReview;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.transaction.annotation.Transactional;
//
//@Slf4j
////@Service
//@Transactional
//@RequiredArgsConstructor
//public class PrReviewServicelegacy {
//
//	public void deletePrReview(Long reviewId, Long userId) {
//
//		validateUserExists(userId);
//
//		PrReview review = prReviewRepository.findById(reviewId)
//			.orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));
//
//		if (!review.getUser().getId().equals(userId)) {
//			throw new MoyoException(CommonErrorCode.PR_REVIEW_DELETE_FORBIDDEN);
//		}
//
//		prReviewHitsRepository.deleteByPrReview(review);
//
//		prReviewRepository.delete(review);
//	}
//}
