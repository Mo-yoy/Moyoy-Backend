package com.moyo.backend.domain.auth.support;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.moyo.backend.domain.auth.oauth.dto.GithubOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserContextMDCFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String ip = getClientIp(request);
			if (ip != null) {
				MDC.put("ip", ip);
			}

			String userId = getUserIdFromSecurityContext();
			if (userId != null) {
				MDC.put("userId", userId);
			}

			String userAgent = request.getHeader("User-Agent");
			if (userAgent != null) {
				MDC.put("userAgent", userAgent);
			}

			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private String getClientIp(HttpServletRequest request) {

		String xff = request.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isEmpty()) {
			return xff.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	private String getUserIdFromSecurityContext() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
			return "anonymous";
		}

		Object principal = auth.getPrincipal();

		if (principal instanceof GithubOAuth2User) {
			return ((GithubOAuth2User)principal).getId().toString();
		}
		return principal.toString();
	}

}
