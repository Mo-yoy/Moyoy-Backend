package com.moyo.backend.domain.security.jwt.filter;

import static com.moyo.backend.common.constant.MoyoConstants.JSON;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyo.backend.common.exception.MoyoException;
import com.moyo.backend.common.response.ApiResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionHandleFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		try {
			filterChain.doFilter(request, response);
		} catch (MoyoException ex) {
			setErrorResponse(ex, response);
		}
	}

	private void setErrorResponse(MoyoException ex, HttpServletResponse response) throws IOException {

		response.setStatus(ex.getErrorReason().getStatus());
		response.setContentType(JSON);
		response.setCharacterEncoding("UTF-8");

		String jsonResponse = objectMapper.writeValueAsString(ApiResponse.fail(ex.getErrorReason()));
		response.getWriter().write(jsonResponse);
	}
}
