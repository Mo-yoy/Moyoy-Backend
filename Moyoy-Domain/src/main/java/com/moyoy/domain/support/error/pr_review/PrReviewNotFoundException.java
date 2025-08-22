package com.moyoy.domain.support.error.pr_review;

import com.moyoy.domain.support.error.MoyoException;

import static com.moyoy.domain.support.error.pr_review.PrReviewErrorCode.PR_REVIEW_NOT_FOUND;

public class PrReviewNotFoundException extends MoyoException {

    public PrReviewNotFoundException() { super(PR_REVIEW_NOT_FOUND); }
}
