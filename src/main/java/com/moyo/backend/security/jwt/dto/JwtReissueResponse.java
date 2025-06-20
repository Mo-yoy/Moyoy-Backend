package com.moyo.backend.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtReissueResponse {

	private String accessToken;
}
