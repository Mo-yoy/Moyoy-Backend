package com.moyoy.domain.pr_review;

import com.moyoy.domain.support.page.PageData;
import com.moyoy.domain.support.page.SliceResult;

import java.util.Optional;

public interface PrReviewRepository {

	PrReview save(PrReview newPrReview);

	Optional<PrReview> findById(Long reviewId);

	SliceResult<PrReview> findAllByStatusAndPosition(Status status, Position position, PageData pageable);

	SliceResult<PrReview> findAllByUserIdAndStatusAndPosition(Long userId, Status status, Position position, PageData pageable);

	Long update(PrReview prReview);

	void deleteById(Long reviewId);


}
