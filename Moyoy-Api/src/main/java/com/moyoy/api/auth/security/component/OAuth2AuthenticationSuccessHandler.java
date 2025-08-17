package com.moyoy.api.auth.security.component;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.moyoy.api.auth.jwt.support.HashUtil;
import com.moyoy.api.auth.jwt.support.JwtPayloadExtractor;
import com.moyoy.api.auth.jwt.support.JwtProvider;
import com.moyoy.api.auth.jwt.support.JwtRefreshTokenEntity;
import com.moyoy.api.auth.jwt.support.JwtRefreshTokenRepository;
import com.moyoy.api.auth.jwt.support.JwtType;
import com.moyoy.api.auth.jwt.support.JwtUserInfo;
import com.moyoy.api.common.util.CookieUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *  사용자가 OAuth를 이용해 직접 인증 성공 후에 호출
 *
 *  해당 클래스는 조금 고려해 볼 게 있어서 추후 다시 리팩토링 함.
 */

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtProvider jwtProvider;
	private final JwtPayloadExtractor jwtPayloadExtractor;
	private final CookieUtils cookieUtils;
	private final String frontLoginSuccessURI;
	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

	public OAuth2AuthenticationSuccessHandler(
		CookieUtils cookieUtils,
		JwtProvider jwtProvider,
		JwtPayloadExtractor jwtPayloadExtractor,
		JwtRefreshTokenRepository jwtRefreshTokenRepository,
		@Value("${spring.login.default-uri}") String frontLoginSuccessURI) {

		this.cookieUtils = cookieUtils;
		this.jwtProvider = jwtProvider;
		this.jwtPayloadExtractor = jwtPayloadExtractor;
		this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
		this.frontLoginSuccessURI = frontLoginSuccessURI;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		JwtUserInfo jwtUserInfo = JwtUserInfo.from(authentication);
		String jwtRefreshToken = jwtProvider.createJwtToken(jwtUserInfo, JwtType.REFRESH);
		response.addHeader(SET_COOKIE, cookieUtils.createJwtRefreshTokenCookie(jwtRefreshToken).toString());

		Long userId = jwtUserInfo.userId();
		LocalDateTime expirationTime = jwtPayloadExtractor.extractExpirationTime(jwtRefreshToken);
		String refreshTokenHash = HashUtil.sha256Base64(jwtRefreshToken);

		jwtRefreshTokenRepository.save(JwtRefreshTokenEntity.of(userId, refreshTokenHash, expirationTime));

		log.info("GitHub OAuth 로그인 성공, 사용자 ID: {} - JWT 발급 진행", jwtUserInfo.userId());

		/**
		 *   스프린트 1의 요구사항을 반영하여 GitHub OAuth 인증 후 사용자를 무조건 Default URI로 리다이렉트 함.
		 *   추후, 요구사항에 따라서 쿠키에 Redirect 경로를 추가해 처리하거나 Request Cache를 사용해서 기능을 확장할 예정
		 */
		response.sendRedirect(frontLoginSuccessURI);
	}
}
