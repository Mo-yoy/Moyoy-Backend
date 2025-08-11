package com.moyoy.api.support.validator;

import com.moyoy.api.support.annotation.LastFetchedUserId;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LastFetchedUserIdValidator implements ConstraintValidator<LastFetchedUserId, Long> {

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {

		return value >= 0;
	}
}
