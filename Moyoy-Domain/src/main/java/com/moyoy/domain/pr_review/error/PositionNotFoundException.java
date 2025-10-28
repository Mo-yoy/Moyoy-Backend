package com.moyoy.domain.pr_review.error;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.POSITION_NOT_FOUND;

import com.moyoy.common.error.MoyoException;

public class PositionNotFoundException extends MoyoException {
	public PositionNotFoundException() {
		super(POSITION_NOT_FOUND);
	}
}
