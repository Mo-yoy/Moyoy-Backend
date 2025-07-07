package com.moyo.backend.domain.pr_review.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;

import com.moyo.backend.common.exception.CommonErrorCode;
import com.moyo.backend.common.exception.MoyoException;
import com.moyo.backend.domain.pr_review.dto.request.PrReviewUpdateRequestDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewUpdateFormResponseDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewUpdateResponseDto;
import com.moyo.backend.domain.pr_review.implement.Position;
import com.moyo.backend.domain.pr_review.implement.PrReview;

@Slf4j
//@Service
@Transactional
@RequiredArgsConstructor
public class PrReviewServicelegacy {

	public PrReviewUpdateFormResponseDto getUpdateForm(Long reviewId, Long userId) {

		validateUserExists(userId);

		PrReview review = prReviewRepository.findById(reviewId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

		if (!review.getUser().getId().equals(userId)) {
			throw new MoyoException(CommonErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
		}

		return new PrReviewUpdateFormResponseDto(review.getTitle(), review.getPosition().toString(), review.getPrUrl(), review.getContent());
	}

	public PrReviewUpdateResponseDto updatePrReview(PrReviewUpdateRequestDto requestDto, Long reviewId, Long userId) {

		validateUserExists(userId);

		PrReview review = prReviewRepository.findById(reviewId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

		if (!review.getUser().getId().equals(userId)) {
			throw new MoyoException(CommonErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
		}

		review.updateDetail(
			requestDto.getTitle(),
			requestDto.getContent(),
			requestDto.getPrUrl(),
			Position.from(requestDto.getPosition()));

		return new PrReviewUpdateResponseDto(reviewId);
	}

	public void deletePrReview(Long reviewId, Long userId) {

		validateUserExists(userId);

		PrReview review = prReviewRepository.findById(reviewId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

		if (!review.getUser().getId().equals(userId)) {
			throw new MoyoException(CommonErrorCode.PR_REVIEW_DELETE_FORBIDDEN);
		}

		prReviewHitsRepository.deleteByPrReview(review);

		prReviewRepository.delete(review);
	}
}
