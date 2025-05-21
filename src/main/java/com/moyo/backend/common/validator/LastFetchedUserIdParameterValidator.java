package com.moyo.backend.common.validator;

import com.moyo.backend.common.annotation.LastFetchedUserId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LastFetchedUserIdParameterValidator implements ConstraintValidator<LastFetchedUserId, Long> {

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        // null이면 검증 통과 (required=false인 경우 고려)
        return value >= 0;
    }
}