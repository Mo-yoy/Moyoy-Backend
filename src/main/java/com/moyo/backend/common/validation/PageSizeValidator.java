package com.moyo.backend.common.validation;

import com.moyo.backend.common.validation.annotation.ValidPageSize;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageSizeValidator implements ConstraintValidator<ValidPageSize, Integer> {

	private static final int MIN = 1;
	private static final int MAX = 100;

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return value >= MIN && value <= MAX;
	}
}
