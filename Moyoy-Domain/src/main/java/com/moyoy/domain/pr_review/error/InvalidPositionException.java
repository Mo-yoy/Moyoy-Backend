package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.INVALID_POSITION;

import com.moyoy.common.error.MoyoException;

public class InvalidPositionException extends MoyoException {
	public InvalidPositionException() {
		super(INVALID_POSITION);
	}
}
