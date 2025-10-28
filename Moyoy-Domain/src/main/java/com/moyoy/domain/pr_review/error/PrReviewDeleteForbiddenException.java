package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.PR_REVIEW_DELETE_FORBIDDEN;

import com.moyoy.common.error.MoyoException;

public class PrReviewDeleteForbiddenException extends MoyoException {
	public PrReviewDeleteForbiddenException() {
		super(PR_REVIEW_DELETE_FORBIDDEN);
	}
}
