package com.moyo.backend.domain.auth.jwt.implement;

import org.springframework.security.core.Authentication;

import com.moyo.backend.domain.auth.oauth.dto.GithubOAuth2User;

public record JwtUserInfo(
	Long userId,
	String authority) {

	public static JwtUserInfo from(Authentication authentication) {

		GithubOAuth2User githubOAuth2User = (GithubOAuth2User)authentication.getPrincipal();
		Long userId = githubOAuth2User.getId();
		String authority = githubOAuth2User.getAuthorities().toString();

		return new JwtUserInfo(userId, authority);
	}
}
