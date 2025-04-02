package com.moyo.backend.pr_review.infrastructure;

import com.moyo.backend.pr_review.application.port.PrReviewRepository;
import com.moyo.backend.pr_review.domain.PrReview;
import com.moyo.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PrReviewRepositoryImpl implements PrReviewRepository {

    private final PrReviewJpaRepository prReviewJpaRepository;

    @Override
    public Page<PrReview> findAllByStatusAndPosition(Boolean status, String position, Pageable pageable) {
        return prReviewJpaRepository.findAllByStatusAndPosition(status, position, pageable);
    }

    @Override
    public Page<PrReview> findAllByUserAndStatusAndPosition(User user, Boolean status, String position, Pageable pageable) {
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
