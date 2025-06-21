package com.moyo.backend.domain.security.jwt.controller;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyo.backend.common.response.ApiResponse;
import com.moyo.backend.common.util.CookieUtils;
import com.moyo.backend.domain.security.jwt.dto.JwtReissueResponse;
import com.moyo.backend.domain.security.jwt.service.JwtReissueService;

@RestController
@RequiredArgsConstructor
public class JwtReissueController {

	private final CookieUtils cookieUtils;
	private final JwtReissueService jwtReissueService;

	@PostMapping("/auth/reissue/token")
	public ResponseEntity<ApiResponse<?>> reissueJwtTokens(@CookieValue(value = "refresh", defaultValue = "") String jwtRefreshToken) {

		Map<String, String> reIssueTokens = jwtReissueService.reIssueJwt(jwtRefreshToken);

		return ResponseEntity.status(OK)
			.header(SET_COOKIE, cookieUtils.createJwtRefreshTokenCookie(reIssueTokens.get(JWT_REFRESH_TYPE)).toString())
			.body(ApiResponse.success(new JwtReissueResponse(reIssueTokens.get(JWT_ACCESS_TYPE))));
	}
}
