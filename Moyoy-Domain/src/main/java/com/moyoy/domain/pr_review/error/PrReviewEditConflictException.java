package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.*;

import com.moyoy.common.error.MoyoException;

public class PrReviewEditConflictException extends MoyoException {
    public PrReviewEditConflictException() {
        super(PR_REVIEW_EDIT_CONFLICT);
    }
}
