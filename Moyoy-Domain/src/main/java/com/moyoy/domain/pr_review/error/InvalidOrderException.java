package com.moyoy.domain.pr_review.error;

import com.moyoy.common.error.MoyoException;

import static com.moyoy.domain.pr_review.error.PrReviewErrorCode.*;

public class InvalidOrderException extends MoyoException {
    public InvalidOrderException() { super(INVALID_ORDER); }
}
