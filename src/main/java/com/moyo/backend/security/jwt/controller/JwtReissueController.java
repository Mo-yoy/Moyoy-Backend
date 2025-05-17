package com.moyo.backend.security.jwt.controller;


import com.moyo.backend.common.dto.ApiResponse;
import com.moyo.backend.common.util.CookieUtils;
import com.moyo.backend.security.jwt.dto.JwtReissueResponse;
import com.moyo.backend.security.jwt.service.JwtReissueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.moyo.backend.common.constant.MoyoConstants.*;

@Tag(name = "1. 토큰 재발급 🔐")
@RestController
@RequiredArgsConstructor
public class JwtReissueController{

    private final CookieUtils cookieUtils;
    private final JwtReissueService jwtReissueService;

    @Operation(summary = "JWT 토큰 재발급", description = "저장된 리프레시 토큰을 통해 새로운 액세스 토큰을 응답 본문에 담아 반환 하고, 새로운 리프레시 토큰은 Set-Cookie 헤더를 통해 전달 합니다.")
    @PostMapping("/auth/reissue/token")
    public ResponseEntity<ApiResponse<?>> reissueJwtTokens(@Parameter(hidden = true) @CookieValue(value = "refresh", defaultValue = "") String jwtRefreshToken){

        Map<String, String> reIssueTokens = jwtReissueService.reIssueJwt(jwtRefreshToken);

        return ResponseEntity.status(OK)
                .header(SET_COOKIE, cookieUtils.createJwtRefreshTokenCookie(reIssueTokens.get(JWT_REFRESH_TYPE)).toString())
                .body(ApiResponse.success(new JwtReissueResponse(reIssueTokens.get(JWT_ACCESS_TYPE))));
    }
}