package com.moyoy.api.auth.security.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.moyoy.domain.user.implement.User;

/**
 *  Spring Security 에서 요구하는 OAuth2User
 *
 *  추후, Security Context Holder의 User Principal이 됨
 */

@RequiredArgsConstructor
public class GithubOAuth2User implements OAuth2User {

	private final Set<GrantedAuthority> authorities;
	private final Map<String, Object> attributes;

	public static GithubOAuth2User from(User moyoUser) {

		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority(moyoUser.getRole().getValue()));

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", moyoUser.getId());

		return new GithubOAuth2User(authorities, attributes);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/// OAuth Authorized Client Id
	@Override
	public String getName() {
		return String.valueOf(attributes.get("id"));
	}

	public Long getId() {
		return (Long)attributes.get("id");
	}
}
