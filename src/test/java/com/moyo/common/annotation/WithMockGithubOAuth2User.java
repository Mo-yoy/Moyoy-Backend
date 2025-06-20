package com.moyo.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockGithubOAuth2UserSecurityContextFactory.class)
public @interface WithMockGithubOAuth2User{
	String id() default "1";
}
