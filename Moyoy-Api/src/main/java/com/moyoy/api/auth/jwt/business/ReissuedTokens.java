package com.moyoy.api.auth.jwt.business;

public record ReissuedTokens(
	String accessToken,
	String refreshToken) {
}
