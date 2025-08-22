package com.moyoy.domain.support.error.pr_review;

import com.moyoy.domain.support.error.MoyoException;

import static com.moyoy.domain.support.error.pr_review.PrReviewErrorCode.STATUS_NOT_FOUND;

public class StatusNotFoundException extends MoyoException {
    public StatusNotFoundException() { super(STATUS_NOT_FOUND); }
}
