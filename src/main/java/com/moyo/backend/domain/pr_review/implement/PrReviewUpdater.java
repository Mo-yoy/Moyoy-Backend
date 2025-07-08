package com.moyo.backend.domain.pr_review.implement;

import com.moyo.backend.common.exception.CommonErrorCode;
import com.moyo.backend.common.exception.MoyoException;
import com.moyo.backend.domain.pr_review.business.dto.PrReviewUpdateResult;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.pr_review.data_access.PrReviewRepository;
import com.moyo.backend.domain.pr_review.implement.dto.PrReviewCreateData;

import jakarta.transaction.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class PrReviewUpdater {

	private final PrReviewRepository prReviewRepository;

	public PrReviewCreateData savePrReview(PrReview prReview) {
		PrReview savedPr = prReviewRepository.save(prReview);

		return new PrReviewCreateData(savedPr.getId());
	}
}
