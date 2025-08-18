package com.moyoy.api.auth.jwt.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtType {

	ACCESS("access"),
	REFRESH("refresh");

	private final String value;
}
