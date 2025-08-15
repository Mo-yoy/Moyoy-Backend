package com.moyoy.api.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.moyoy.api.common.validator.PageSizeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PageSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPageSize{
	String message() default "pageSize는 1 이상 100 이하의 값이어야 합니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
