package com.moyo.backend.common.validator;

import com.moyo.backend.common.annotation.LastFetchedUserId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LastFetchedUserIdValidator implements ConstraintValidator<LastFetchedUserId, Long> {

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        return value >= 0;
    }
}