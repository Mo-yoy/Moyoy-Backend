package com.moyo.backend.common.validation.annotation;

import java.lang.annotation.*;

import com.moyo.backend.common.validation.LastFetchedUserIdValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = LastFetchedUserIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LastFetchedUserId{
	String message() default "마지막으로 조회한 사용자의 UserId는 0 이상의 값이어야 합니다";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
