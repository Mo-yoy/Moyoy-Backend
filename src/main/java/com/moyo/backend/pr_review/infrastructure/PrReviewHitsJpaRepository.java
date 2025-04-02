package com.moyo.backend.pr_review.infrastructure;

import com.moyo.backend.pr_review.domain.PrReview;
import com.moyo.backend.pr_review.domain.PrReviewHits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrReviewHitsJpaRepository extends JpaRepository<PrReviewHits, Long> {

    boolean existsByPrReviewIdAndUserId(Long prReviewRequestId, Long userId);

    void deleteByPrReview(PrReview prReview);
}
