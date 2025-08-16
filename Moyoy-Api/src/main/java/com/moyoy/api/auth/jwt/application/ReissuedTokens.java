package com.moyoy.api.auth.jwt.application;

public record ReissuedTokens(
	String accessToken,
	String refreshToken) {
}
