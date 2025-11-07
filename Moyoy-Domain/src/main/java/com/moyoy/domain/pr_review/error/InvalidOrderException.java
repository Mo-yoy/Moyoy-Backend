package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.*;

import com.moyoy.common.error.MoyoException;

public class InvalidOrderException extends MoyoException {
	public InvalidOrderException() {
		super(INVALID_ORDER);
	}
}
