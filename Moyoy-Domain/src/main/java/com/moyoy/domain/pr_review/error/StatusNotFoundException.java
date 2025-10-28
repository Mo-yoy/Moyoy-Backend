package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.STATUS_NOT_FOUND;

import com.moyoy.common.error.MoyoException;

public class StatusNotFoundException extends MoyoException {
	public StatusNotFoundException() {
		super(STATUS_NOT_FOUND);
	}
}
