package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.PR_REVIEW_NOT_FOUND;

import com.moyoy.common.error.MoyoException;

public class PrReviewNotFoundException extends MoyoException {

	public PrReviewNotFoundException() {
		super(PR_REVIEW_NOT_FOUND);
	}
}
