package com.moyo.common.annotation;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.moyo.backend.domain.auth.oauth.dto.GithubOAuth2User;

public class WithMockMoyoyUserSecurityContextFactory implements WithSecurityContextFactory<WithMockMoyoyUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockMoyoyUser annotation) {
		Map<String, Object> attributes = Map.of("id", annotation.id());
		GithubOAuth2User principal = new GithubOAuth2User(Collections.emptySet(), attributes);
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);
		return context;
	}
}
