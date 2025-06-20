package com.moyo.backend.security.oauth;

import static com.moyo.backend.common.constant.MoyoConstants.FORBIDDEN;
import static com.moyo.backend.common.constant.MoyoConstants.JSON;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyo.backend.common.dto.ApiResponse;
import com.moyo.backend.security.jwt.exception.AuthErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

		response.setStatus(FORBIDDEN);
		response.setContentType(JSON);
		response.setCharacterEncoding("UTF-8");

		log.error("Access Denied Handler 예외 처리 : {}", accessDeniedException.getMessage());

		String jsonResponse = objectMapper.writeValueAsString(ApiResponse.fail(AuthErrorCode.ACCESS_DENIED.getErrorReason()));
		response.getWriter().write(jsonResponse);
	}
}
