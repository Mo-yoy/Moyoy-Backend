package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.INVALID_STATUS;

import com.moyoy.common.error.MoyoException;

public class InvalidStatusException extends MoyoException {
	public InvalidStatusException() {
		super(INVALID_STATUS);
	}
}
