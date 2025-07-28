package com.moyo.backend.domain.auth.support;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestInfoMDCFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		try {
			MDC.put("requestUri", request.getRequestURI());
			MDC.put("httpMethod", request.getMethod());
			MDC.put("queryString", request.getQueryString() != null ? request.getQueryString() : "");
			MDC.put("referer", request.getHeader("Referer") != null ? request.getHeader("Referer") : "");
			MDC.put("host", request.getHeader("Host") != null ? request.getHeader("Host") : "");

			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}
}
