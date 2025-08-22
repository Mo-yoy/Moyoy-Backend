package com.moyoy.domain.support.error.pr_review;

import com.moyoy.domain.support.error.MoyoException;

import static com.moyoy.domain.support.error.pr_review.PrReviewErrorCode.PR_REVIEW_EDIT_FORBIDDEN;

public class PrReviewEditForbiddenException extends MoyoException {
    public PrReviewEditForbiddenException() {
        super(PR_REVIEW_EDIT_FORBIDDEN);
    }
}
