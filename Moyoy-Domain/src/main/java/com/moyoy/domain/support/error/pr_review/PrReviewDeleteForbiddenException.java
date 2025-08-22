package com.moyoy.domain.support.error.pr_review;

import com.moyoy.domain.support.error.MoyoException;

import static com.moyoy.domain.support.error.pr_review.PrReviewErrorCode.PR_REVIEW_DELETE_FORBIDDEN;

public class PrReviewDeleteForbiddenException extends MoyoException {
    public PrReviewDeleteForbiddenException() {
        super(PR_REVIEW_DELETE_FORBIDDEN);
    }
}
