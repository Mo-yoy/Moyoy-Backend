package com.moyo.backend.security.jwt.controller;


import com.moyo.backend.common.dto.ApiResponse;
import com.moyo.backend.common.util.CookieUtils;
import com.moyo.backend.security.jwt.dto.JwtReissueResponse;
import com.moyo.backend.security.jwt.exception.JwtTokenNotExistException;
import com.moyo.backend.security.jwt.service.JwtReissueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.moyo.backend.common.constant.MoyoConstants.*;

@RestController
@RequiredArgsConstructor
public class JwtReissueController {

    private final CookieUtils cookieUtils;
    private final JwtReissueService jwtReissueService;

    @PostMapping("/auth/reissue/token")
    public ResponseEntity<ApiResponse<?>> reissueJwtTokens(@CookieValue(value = "refresh", defaultValue = "") String jwtRefreshToken){

        if (jwtRefreshToken.isEmpty()) throw new JwtTokenNotExistException();

        Map<String, String> reIssueTokens = jwtReissueService.reIssueJwt(jwtRefreshToken);

        return ResponseEntity.status(OK)
                .header(SET_COOKIE, cookieUtils.createJwtRefreshTokenCookie(reIssueTokens.get(JWT_REFRESH_TYPE)).toString())
                .body(ApiResponse.success(new JwtReissueResponse(reIssueTokens.get(JWT_ACCESS_TYPE))));
    }
}
