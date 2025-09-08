package com.moyoy.api.auth.jwt.application.response;

public record ReissueJwtResult(
	String accessToken,
	String refreshToken) {
}
