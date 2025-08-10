package com.moyoy.api.common.util;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.common.exception.BaseErrorCode;

import jakarta.servlet.http.HttpServletResponse;

/**
 *  ControllerAdvice가 아닌 곳에서 에러를 직접 처리할 때 사용
 *
 *  ex) Filter
 */

@Component
@RequiredArgsConstructor
public class ErrorResponseWriter {

	private final ObjectMapper objectMapper;

	public void writeErrorResponse(HttpServletResponse response, int HttpStatusCode, BaseErrorCode errorCode) throws IOException {

		response.setStatus(HttpStatusCode);
		response.setContentType(JSON);
		response.setCharacterEncoding(UTF_8);

		String jsonResponse = objectMapper.writeValueAsString(ApiResponse.fail(errorCode.getErrorReason()));
		response.getWriter().write(jsonResponse);
	}
}
