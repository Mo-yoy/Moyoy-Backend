package com.moyoy.api.auth.security.component;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.moyoy.api.common.util.ErrorResponseWriter;
import com.moyoy.common.exception.auth.AuthErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *  OAuth를 이용한 사용자의 직접적인 인증 과정에서 발생한 에러 처리, AuthenticationEntryPoint와 다름
 *
 *  에러 발생 지점 : OAuth2LoginAuthenticationFilter가 상속 하는 AbstractAuthenticationProcessingFilter
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final ErrorResponseWriter errorResponseWriter;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

		log.error("OAuth 인증 필터 에서 인증 처리 중 예외 발생, OAuth 인증과정에서 인증에 실패했습니다.", exception);
		errorResponseWriter.writeErrorResponse(response, UNAUTHORIZED, AuthErrorCode.UNAUTHORIZED_USER);
	}
}
