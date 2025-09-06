package com.moyoy.common.annotation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.moyoy.api.auth.security.principal.GithubOAuth2User;

import com.moyoy.domain.user.Role;

public class WithMockMoyoyUserSecurityContextFactory implements WithSecurityContextFactory<WithMockMoyoyUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockMoyoyUser annotation) {

		Map<String, Object> attributes = Map.of("id", annotation.id());
		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority(Role.USER.getValue()));

		GithubOAuth2User principal = new GithubOAuth2User(authorities, attributes);
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);

		return context;
	}
}
