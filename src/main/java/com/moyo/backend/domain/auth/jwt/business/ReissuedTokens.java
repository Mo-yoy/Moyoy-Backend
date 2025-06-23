package com.moyo.backend.domain.auth.jwt.business;

public record ReissuedTokens(
	String accessToken,
	String refreshToken) {
}
