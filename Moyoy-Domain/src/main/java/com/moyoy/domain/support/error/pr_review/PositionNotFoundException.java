package com.moyoy.domain.support.error.pr_review;

import com.moyoy.domain.support.error.MoyoException;

import static com.moyoy.domain.support.error.pr_review.PrReviewErrorCode.POSITION_NOT_FOUND;

public class PositionNotFoundException extends MoyoException {
    public PositionNotFoundException() {
        super(POSITION_NOT_FOUND);
    }
}
