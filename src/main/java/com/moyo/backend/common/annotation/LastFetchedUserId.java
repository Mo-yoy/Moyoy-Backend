package com.moyo.backend.common.annotation;

import com.moyo.backend.common.validator.LastFetchedUserIdParameterValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LastFetchedUserIdParameterValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LastFetchedUserId {
    String message() default "마지막으로 조회한 사용자의 UserId는 0 이상의 값이어야 합니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}