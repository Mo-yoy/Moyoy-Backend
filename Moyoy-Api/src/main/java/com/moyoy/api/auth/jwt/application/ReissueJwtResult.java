package com.moyoy.api.auth.jwt.application;

public record ReissueJwtResult(
	String accessToken,
	String refreshToken) {
}
