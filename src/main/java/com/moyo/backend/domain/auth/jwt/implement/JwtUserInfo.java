package com.moyo.backend.domain.auth.jwt.implement;

public record JwtUserInfo(
	Long userId,
	String authority) {
}
