package com.moyoy.api.common.filter;

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

	private static final String REQUEST_URI = "requestUri";
	private static final String HTTP_METHOD = "httpMethod";
	private static final String QUERY_STRING = "queryString";
	private static final String REFERER = "Referer";
	private static final String HOST = "Host";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException,
			IOException {
		try {
			MDC.put(REQUEST_URI, request.getRequestURI());
			MDC.put(HTTP_METHOD, request.getMethod());
			MDC.put(QUERY_STRING, getHeaderOrDefault(request.getQueryString(), ""));
			MDC.put(REFERER, getHeaderOrDefault(request.getHeader(REFERER), ""));
			MDC.put(HOST, getHeaderOrDefault(request.getHeader(HOST), ""));

			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private String getHeaderOrDefault(String headerValue, String defaultValue) {
		return headerValue != null ? headerValue : defaultValue;
	}
}
