package com.moyo.backend.domain.security.oauth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.moyo.backend.domain.user.domain.Role;
import com.moyo.backend.domain.user.domain.User;

public class GithubOAuth2UserConverter {

	private static final String GITHUB_OAUTH2_USER_ID = "id";

	public static GithubOAuth2User convert(OAuth2User oAuth2User, User user) {

		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority(user == null ? Role.USER.getValue() : user.getRole().getValue()));

		// GitHub 사용자 정보 중 Principal에 유지할 정보 추출
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", oAuth2User.getAttribute(GITHUB_OAUTH2_USER_ID));

		return new GithubOAuth2User(authorities, attributes);
	}
}
