package com.moyoy.api.auth.jwt.presentation;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;


@Component
public class RefreshTokenCookieFactory {

	private final String domain;
	private final String sameSite;

	public RefreshTokenCookieFactory(
		@Value("${spring.cookie.domain}") String domain,
		@Value("${spring.cookie.samesite:Strict}") String sameSite
	) {
		this.domain = domain;
		this.sameSite = sameSite;
	}

	public ResponseCookie createRefreshTokenCookie(String refreshToken) {

		Duration maxAge = Duration.ofMillis(JWT_REFRESH_TOKEN_EXPIRATION_MINUTE_MS).minusMinutes(1);

		return ResponseCookie.from("refresh", refreshToken)
			.path("/")
			.httpOnly(true)
			.secure(true)
			.sameSite(sameSite)
			.domain(domain)
			.maxAge(maxAge)
			.build();
	}

}
