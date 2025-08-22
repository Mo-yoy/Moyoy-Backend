package com.moyoy.api.pr_review.application.response;

import com.moyoy.domain.pr_review.PrReview;

public record PrReviewContentResult(
        String title,
        String position,
        String prUrl,
        String content) {
    public static PrReviewContentResult from(PrReview prReview) {
        return new PrReviewContentResult(
                prReview.getTitle(),
                prReview.getPosition().getValue(),
                prReview.getPrUrl(),
                prReview.getContent()
        );
    }
}
