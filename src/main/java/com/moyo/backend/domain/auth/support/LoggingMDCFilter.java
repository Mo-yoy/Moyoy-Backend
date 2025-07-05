package com.moyo.backend.domain.auth.support;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.moyo.backend.domain.auth.oauth.dto.GithubOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class LoggingMDCFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		try {
			HttpServletRequest req = (HttpServletRequest) request;
			String ip = getClientIp(req);
			MDC.put("ip", ip);

			String userId = getUserIdFromSecurityContext();
			MDC.put("userId", userId);

			chain.doFilter(request, response);
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
