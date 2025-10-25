package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.PR_REVIEW_EDIT_FORBIDDEN;

import com.moyoy.common.error.MoyoException;

public class PrReviewEditForbiddenException extends MoyoException {
	public PrReviewEditForbiddenException() {
		super(PR_REVIEW_EDIT_FORBIDDEN);
	}
}
