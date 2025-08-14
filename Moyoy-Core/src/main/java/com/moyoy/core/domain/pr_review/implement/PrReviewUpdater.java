package com.moyoy.core.domain.pr_review.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.core.db_access.domain.pr_review.PrReviewRepository;
import com.moyoy.core.domain.pr_review.implement.dto.PrReviewCreateData;

import jakarta.transaction.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class PrReviewUpdater {

	private final PrReviewRepository prReviewRepository;

	public PrReviewCreateData create(PrReview prReview) {
		PrReview savedPr = prReviewRepository.save(prReview);

		return new PrReviewCreateData(savedPr.getId());
	}

	public void delete(PrReview prReview) {
		prReviewRepository.delete(prReview);
	}
}
