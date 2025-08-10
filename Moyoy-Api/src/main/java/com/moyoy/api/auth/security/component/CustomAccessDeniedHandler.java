package com.moyoy.api.auth.security.component;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.moyoy.api.common.util.ErrorResponseWriter;
import com.moyoy.common.exception.auth.AuthErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *  JWT ACCESS TOKEN 기반 인가 에러 처리
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ErrorResponseWriter errorResponseWriter;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

		log.warn("Access Denied Handler 인가 예외 처리 : {}", accessDeniedException.getMessage());
		errorResponseWriter.writeErrorResponse(response, FORBIDDEN, AuthErrorCode.ACCESS_DENIED);
	}
}
