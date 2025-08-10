package com.moyoy.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockMoyoyUserSecurityContextFactory.class)
public @interface WithMockMoyoyUser{
	long id() default 1;
}
