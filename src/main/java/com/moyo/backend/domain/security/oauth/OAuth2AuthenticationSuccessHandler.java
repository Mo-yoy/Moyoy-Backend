package com.moyo.backend.domain.security.oauth;

import static com.moyo.backend.common.constant.MoyoConstants.JWT_REFRESH_TYPE;
import static com.moyo.backend.common.constant.MoyoConstants.SET_COOKIE;

import java.io.IOException;
import java.text.ParseException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.moyo.backend.common.util.CookieUtils;
import com.moyo.backend.domain.security.jwt.domain.JwtRefreshToken;
import com.moyo.backend.domain.security.jwt.repository.JwtRefreshTokenRepository;
import com.moyo.backend.domain.security.jwt.util.JwtProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final OctetSequenceKey jwk;
	private final JwtProvider jwtProvider;
	private final CookieUtils cookieUtils;
	private final String frontLoginSuccessURI;
	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

	public OAuth2AuthenticationSuccessHandler(CookieUtils cookieUtils, OctetSequenceKey jwk, JwtProvider jwtProvider,
		JwtRefreshTokenRepository jwtRefreshTokenRepository, @Value("${spring.login.default-uri}") String frontLoginSuccessURI) {

		this.cookieUtils = cookieUtils;
		this.jwk = jwk;
		this.jwtProvider = jwtProvider;
		this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
		this.frontLoginSuccessURI = frontLoginSuccessURI;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		String jwtRefreshToken;
		GithubOAuth2User userPrincipal = (GithubOAuth2User)authentication.getPrincipal();

		try {
			log.info("GitHub OAuth 로그인 성공, 사용자 ID: {} - JWT 발급 진행", userPrincipal.getId());
			jwtRefreshToken = jwtProvider.createJwtToken(userPrincipal, jwk, JWT_REFRESH_TYPE);
			response.addHeader(SET_COOKIE, cookieUtils.createJwtRefreshTokenCookie(jwtRefreshToken).toString());

			SignedJWT signedJWT = SignedJWT.parse(jwtRefreshToken);
			JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

			jwtRefreshTokenRepository.save(JwtRefreshToken.from(claimsSet, jwtRefreshToken));

			/**
			 *   스프린트 1의 요구사항을 반영하여 GitHub OAuth 인증 후 사용자를 무조건 Default URI로 리다이렉트 함.
			 *   추후, 요구사항에 따라서 쿠키에 Redirect 경로를 추가해 처리하거나 Request Cache를 사용해서 기능을 확장할 예정
			 */
			response.sendRedirect(frontLoginSuccessURI);
		} catch (JOSEException | DataAccessException | ParseException ex) {
			throw new RuntimeException(ex);
		}
	}
}
