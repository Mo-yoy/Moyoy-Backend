package com.moyoy.api.auth.jwt.implement;

public record ReissuedTokens(
	String accessToken,
	String refreshToken) {
}
