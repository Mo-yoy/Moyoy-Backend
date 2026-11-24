package com.moyoy.api.support;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

public class ClientIpUtils {

	public static String extract(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader("X-Forwarded-For"))
			.map(xff -> xff.split(",")[0].trim())
			.orElse(request.getRemoteAddr());
	}
}
