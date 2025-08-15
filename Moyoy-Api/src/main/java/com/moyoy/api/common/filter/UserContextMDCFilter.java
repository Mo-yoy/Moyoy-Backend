package com.moyoy.api.common.filter;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.moyoy.api.auth.security.dto.GithubOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserContextMDCFilter extends OncePerRequestFilter {

	private static final String MDC_KEY_IP = "ip";
	private static final String MDC_KEY_USER_ID = "userId";
	private static final String MDC_KEY_USER_AGENT = "userAgent";

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
		throws ServletException,
			IOException {
		try {
			putMDCIfNotNull(MDC_KEY_IP, extractClientIp(request));
			putMDCIfNotNull(MDC_KEY_USER_ID, extractUserIdFromSecurityContext());
			putMDCIfNotNull(MDC_KEY_USER_AGENT, request.getHeader("User-Agent"));

			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private void putMDCIfNotNull(final String key, final String value) {

		Optional.ofNullable(value).ifPresent(v -> MDC.put(key, v));
	}

	private String extractClientIp(final HttpServletRequest request) {

		return Optional.ofNullable(request.getHeader("X-Forwarded-For"))
			.map(xff -> xff.split(",")[0].trim())
			.orElse(request.getRemoteAddr());
	}

	private String extractUserIdFromSecurityContext() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
			return "anonymous";
		}

		Object principal = auth.getPrincipal();

		if (principal instanceof GithubOAuth2User githubUser) {
			return githubUser.getId().toString();
		}

		return principal.toString();
	}

}
