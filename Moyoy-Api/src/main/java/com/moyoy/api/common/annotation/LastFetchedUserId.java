package com.moyoy.api.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.moyoy.api.common.validator.LastFetchedUserIdValidator;

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
