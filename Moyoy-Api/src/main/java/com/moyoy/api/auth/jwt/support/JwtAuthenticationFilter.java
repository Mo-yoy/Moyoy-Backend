package com.moyoy.api.auth.jwt.support;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.moyoy.api.auth.security.dto.GithubOAuth2User;
import com.moyoy.common.exception.auth.JwtTokenExpiredException;
import com.moyoy.common.exception.auth.JwtTokenInvalidException;
import com.moyoy.common.exception.auth.JwtTokenTypeMismatchException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

///  TODO : 리팩토링 필요

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String GITHUB_LOGIN_REDIRECT_URL = "/auth/login/github";
	private static final String GITHUB_LOGIN_AUTHORIZATION_CODE_URL = "/login/oauth2/code/github";
	private static final String TOKEN_REISSUE_URL = "/auth/reissue/token";

	private final MACVerifier macVerifier;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		if (requestURI.equals(GITHUB_LOGIN_REDIRECT_URL) || requestURI.equals(GITHUB_LOGIN_AUTHORIZATION_CODE_URL) || requestURI.equals(TOKEN_REISSUE_URL)) {
			filterChain.doFilter(request, response);
			return;
		}

		String header = request.getHeader(AUTHORIZATION);
		if (header == null) {
			filterChain.doFilter(request, response);
			return;
		}

		if (!header.startsWith("Bearer "))
			throw new JwtTokenInvalidException();
		String accessToken = header.replace("Bearer ", "");
		if (accessToken.isBlank())
			throw new JwtTokenInvalidException();

		try {
			SignedJWT signedJWT = SignedJWT.parse(accessToken);
			boolean verifyResult = signedJWT.verify(macVerifier);
			JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

			if (verifyResult) {
				String type = jwtClaimsSet.getClaim(JWT_CLAIM_TOKEN_TYPE).toString();

				if (type != null && !type.equals(JWT_ACCESS_TYPE))
					throw new JwtTokenTypeMismatchException();
				if (jwtClaimsSet.getExpirationTime() != null && jwtClaimsSet.getExpirationTime().before(new Date()))
					throw new JwtTokenExpiredException();

				String authority = jwtClaimsSet.getClaim(JWT_CLAIM_AUTHORITY).toString();
				Set<GrantedAuthority> authorities = new HashSet<>();
				authorities.add(new SimpleGrantedAuthority(authority));

				Long id = (Long)jwtClaimsSet.getClaim(JWT_CLAIM_USER_ID);
				Map<String, Object> attributes = new HashMap<>();
				attributes.put("id", id);

				GithubOAuth2User userPrincipal = new GithubOAuth2User(authorities, attributes);
				Authentication authentication = new OAuth2AuthenticationToken(userPrincipal, userPrincipal.getAuthorities(), GITHUB_REGISTRATION_ID);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else
				throw new JwtTokenInvalidException();

		} catch (ParseException | JOSEException e) {
			log.warn("JWT 인증 필터에서 JWT 파싱 에러 발생");
			throw new JwtTokenInvalidException();
		}

		filterChain.doFilter(request, response);
	}
}
