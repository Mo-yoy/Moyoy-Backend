package com.moyo.backend.pr_review.infrastructure;

import com.moyo.backend.pr_review.domain.PrReview;
import com.moyo.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrReviewRepository extends JpaRepository<PrReview, Long> {

    // Pageable로 정렬 & 페이징 처리.
    Page<PrReview> findAllByStatusAndPosition(Boolean status, String position, Pageable pageable);

    Page<PrReview> findAllByUserAndStatusAndPosition(User user, Boolean status, String position, Pageable pageable);

    Optional<PrReview> findById(Long reviewId);
}
