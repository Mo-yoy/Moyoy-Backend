package com.moyoy.api.auth.security.component;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.io.IOException;
import java.text.ParseException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.moyoy.api.auth.jwt.implement.JwtProvider;
import com.moyoy.api.auth.jwt.implement.JwtUserInfo;
import com.moyoy.api.common.util.CookieUtils;
import com.moyoy.domain.auth.data_access.JwtRefreshTokenRepository;
import com.moyoy.domain.auth.implement.JwtRefreshToken;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *  사용자가 OAuth를 이용해 직접 인증 성공 후에 호출
 *
 *  해당 클래스는 조금 고려해 볼 게 있어서 추후 다시 리팩토링 함.
 */

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtProvider jwtProvider;
	private final CookieUtils cookieUtils;
	private final String frontLoginSuccessURI;
	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

	public OAuth2AuthenticationSuccessHandler(
		CookieUtils cookieUtils,
		JwtProvider jwtProvider,
		JwtRefreshTokenRepository jwtRefreshTokenRepository,
		@Value("${spring.login.default-uri}") String frontLoginSuccessURI) {

		this.cookieUtils = cookieUtils;
		this.jwtProvider = jwtProvider;
		this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
		this.frontLoginSuccessURI = frontLoginSuccessURI;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		String jwtRefreshToken;
		JwtUserInfo jwtUserInfo = JwtUserInfo.from(authentication);

		try {

			log.info("GitHub OAuth 로그인 성공, 사용자 ID: {} - JWT 발급 진행", jwtUserInfo.userId());
			jwtRefreshToken = jwtProvider.createJwtToken(jwtUserInfo, JWT_REFRESH_TYPE);
			response.addHeader(SET_COOKIE, cookieUtils.createJwtRefreshTokenCookie(jwtRefreshToken).toString());

			SignedJWT signedJWT = SignedJWT.parse(jwtRefreshToken);
			JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
			jwtRefreshTokenRepository.save(JwtRefreshToken.from(claimsSet, jwtRefreshToken));

			/**
			 *   스프린트 1의 요구사항을 반영하여 GitHub OAuth 인증 후 사용자를 무조건 Default URI로 리다이렉트 함.
			 *   추후, 요구사항에 따라서 쿠키에 Redirect 경로를 추가해 처리하거나 Request Cache를 사용해서 기능을 확장할 예정
			 */
			response.sendRedirect(frontLoginSuccessURI);
		} catch (DataAccessException | ParseException ex) {
			throw new RuntimeException(ex);
		}
	}
}
