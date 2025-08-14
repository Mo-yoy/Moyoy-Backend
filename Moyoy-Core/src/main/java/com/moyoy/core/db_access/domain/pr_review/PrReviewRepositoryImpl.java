package com.moyoy.core.db_access.domain.pr_review;

import java.util.Optional;

import com.moyoy.core.domain.pr_review.implement.Position;
import com.moyoy.core.domain.pr_review.implement.PrReview;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrReviewRepositoryImpl implements PrReviewRepository {

	private final PrReviewJpaRepository prReviewJpaRepository;

	@Override
	public Slice<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable) {
		return prReviewJpaRepository.findAllByStatusAndPosition(status, position, pageable);
	}

	@Override
	public Slice<PrReview> findAllByUserIdAndStatusAndPosition(Long userId, Boolean status, Position position, Pageable pageable) {
		return prReviewJpaRepository.findAllByUserIdAndStatusAndPosition(userId, status, position, pageable);
	}

	@Override
	public Optional<PrReview> findById(Long reviewId) {
		return prReviewJpaRepository.findById(reviewId);
	}

	@Override
	public PrReview save(PrReview review) {
		return prReviewJpaRepository.save(review);
	}

	@Override
	public void delete(PrReview review) {
		prReviewJpaRepository.delete(review);
	}
}
