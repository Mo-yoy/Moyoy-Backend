package com.moyoy.api.auth.jwt.presentation;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyoy.api.auth.jwt.business.JwtReissueService;
import com.moyoy.api.auth.jwt.business.ReissuedTokens;
import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.api.common.util.CookieUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class JwtReissueController {

	private final CookieUtils cookieUtils;
	private final JwtReissueService jwtReissueService;

	@PostMapping("/auth/reissue/token")
	public ResponseEntity<ApiResponse<JwtReissueResponse>> reissueJwtTokens(@CookieValue(value = "refresh", defaultValue = "") String jwtRefreshToken) {

		ReissuedTokens reIssueTokens = jwtReissueService.reIssueJwt(jwtRefreshToken);

		String refreshTokenCookie = cookieUtils.createJwtRefreshTokenCookie(reIssueTokens.refreshToken()).toString();
		JwtReissueResponse responseData = new JwtReissueResponse(reIssueTokens.accessToken());

		return ResponseEntity
			.status(OK)
			.header(SET_COOKIE, refreshTokenCookie)
			.body(ApiResponse.success(responseData));
	}
}
