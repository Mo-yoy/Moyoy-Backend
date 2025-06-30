package com.moyo.backend.domain.pr_review.data_access;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.moyo.backend.domain.pr_review.implement.PrReview;
import com.moyo.backend.domain.pr_review.implement.Position;
import com.moyo.backend.domain.user.implement.User;

@Repository
@RequiredArgsConstructor
public class PrReviewRepositoryImpl implements PrReviewRepository {

	private final PrReviewJpaRepository prReviewJpaRepository;

	@Override
	public Page<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable) {
		return prReviewJpaRepository.findAllByStatusAndPosition(status, position, pageable);
	}

	@Override
	public Page<PrReview> findAllByUserAndStatusAndPosition(User user, Boolean status, Position position, Pageable pageable) {
		return prReviewJpaRepository.findAllByUserAndStatusAndPosition(user, status, position, pageable);
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
