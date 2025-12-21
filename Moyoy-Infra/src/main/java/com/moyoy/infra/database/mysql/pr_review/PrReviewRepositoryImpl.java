package com.moyoy.infra.database.mysql.pr_review;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.moyoy.domain.pr_review.PrReview;
import com.moyoy.domain.pr_review.PrReviewRepository;

@Repository
@RequiredArgsConstructor
public class PrReviewRepositoryImpl implements PrReviewRepository {

	private final PrReviewJpaRepository prReviewJpaRepository;

	@Override
	public PrReview save(PrReview prReview) {

		PrReviewEntity prReviewEntity = PrReviewMapper.toEntity(prReview);
		PrReviewEntity newPrReviewEntity = prReviewJpaRepository.save(prReviewEntity);

		return PrReviewMapper.toModel(newPrReviewEntity);
	}

	@Override
	public Optional<PrReview> findById(Long reviewId) {
		return prReviewJpaRepository.findById(reviewId)
			.map(PrReviewMapper::toModel);
	}

	@Override
	public void increaseHitCount(Long reviewId) {
		prReviewJpaRepository.increaseHitCount(reviewId);
	}

	@Override
	public void deleteById(Long reviewId) {
		prReviewJpaRepository.deleteById(reviewId);
	}
}
