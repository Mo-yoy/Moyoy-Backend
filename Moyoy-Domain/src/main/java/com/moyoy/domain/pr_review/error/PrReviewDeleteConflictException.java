package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.PR_REVIEW_DELETE_CONFLICT;

import com.moyoy.common.error.MoyoException;

public class PrReviewDeleteConflictException extends MoyoException {
	public PrReviewDeleteConflictException() {
		super(PR_REVIEW_DELETE_CONFLICT);
	}
}
