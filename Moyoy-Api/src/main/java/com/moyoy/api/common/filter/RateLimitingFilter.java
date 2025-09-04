package com.moyoy.api.common.filter;

import static com.moyoy.common.constant.MoyoConstants.*;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.moyoy.api.common.util.ErrorResponseWriter;
import com.moyoy.domain.support.error.CommonErrorCode;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

	private final ProxyManager<String> proxyManager;
	private final BucketConfiguration configuration;
	private final ErrorResponseWriter errorResponseWriter;

	public RateLimitingFilter(ProxyManager<String> proxyManager,
		BucketConfiguration configuration, ErrorResponseWriter errorResponseWriter) {
		this.proxyManager = proxyManager;
		this.configuration = configuration;
		this.errorResponseWriter = errorResponseWriter;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String key = resolveKey(request);
		Bucket bucket = proxyManager.builder().build(key, () -> configuration);

		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

		if (probe.isConsumed()) {

			response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
			filterChain.doFilter(request, response);
		}
		else {

			long waitNanos = probe.getNanosToWaitForRefill();
			long retryAfterSeconds = TimeUnit.NANOSECONDS.toSeconds(waitNanos) + 1;
			response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
			errorResponseWriter.writeErrorResponse(response, TOO_MANY_REQUEST, CommonErrorCode.TOO_MANY_REQUEST);
		}
	}

	private String resolveKey(HttpServletRequest request) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
			return "token::user::" + auth.getName();
		}

		return "token::ip::" + extractClientIp(request);
	}

	private String extractClientIp(final HttpServletRequest request) {

		return Optional.ofNullable(request.getHeader("X-Forwarded-For"))
			.map(xff -> xff.split(",")[0].trim())
			.orElse(request.getRemoteAddr());
	}
}
