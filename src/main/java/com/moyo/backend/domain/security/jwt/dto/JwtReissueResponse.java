package com.moyo.backend.domain.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtReissueResponse {

	private String accessToken;
}
