package com.moyoy.api.auth.jwt.support.filter;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jose.crypto.MACVerifier;

import com.moyoy.api.auth.error.JwtTokenInvalidException;
import com.moyoy.api.auth.jwt.support.JwtPayloadExtractor;
import com.moyoy.api.auth.jwt.support.JwtType;
import com.moyoy.api.auth.jwt.support.JwtUserInfo;
import com.moyoy.api.auth.jwt.support.JwtValidator;
import com.moyoy.api.auth.security.principal.GithubOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

///  JWT Access Token 검증 필터

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String GITHUB_LOGIN_REDIRECT_URL = "/auth/login/github";
	private static final String GITHUB_LOGIN_AUTHORIZATION_CODE_URL = "/login/oauth2/code/github";
	private static final String TOKEN_REISSUE_URL = "/api/v1/auth/reissue/token";
	private static final String BEARER_PREFIX = "Bearer ";

	private static final Map<String, Set<String>> whiteList = Map.of(
		GET, Set.of(
			GITHUB_LOGIN_REDIRECT_URL,
			GITHUB_LOGIN_AUTHORIZATION_CODE_URL),
		POST, Set.of(
			TOKEN_REISSUE_URL));

	private final MACVerifier macVerifier;
	private final JwtValidator jwtValidator;
	private final JwtPayloadExtractor jwtPayloadExtractor;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String method = request.getMethod();
		String requestURI = request.getRequestURI();

		if (whiteList.getOrDefault(method, Set.of()).contains(requestURI)) {
			filterChain.doFilter(request, response);
			return;
		}

		String authorizationHeader = request.getHeader(AUTHORIZATION);

		/// Access Token이 Null 이면 비인증 사용자로 간주, 뒤에서 처리됨
		if (authorizationHeader == null) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = resolveBearerToken(request);
		authenticate(accessToken);

		filterChain.doFilter(request, response);
	}

	private String resolveBearerToken(HttpServletRequest request) {

		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if (!authorizationHeader.startsWith(BEARER_PREFIX))
			throw new JwtTokenInvalidException();

		return authorizationHeader.substring(BEARER_PREFIX.length());
	}

	private void authenticate(String accessToken) {

		jwtValidator.validate(JwtType.ACCESS, accessToken);

		JwtUserInfo info = jwtPayloadExtractor.extractUserInfo(accessToken);

		Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(info.authority()));
		Map<String, Object> attributes = Map.of("id", info.userId());

		GithubOAuth2User principal = new GithubOAuth2User(authorities, attributes);
		Authentication authentication = new OAuth2AuthenticationToken(principal, principal.getAuthorities(), GITHUB_REGISTRATION_ID);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
