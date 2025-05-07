package com.moyo.backend.securityLegacy.jwt.controller;


import com.moyo.backend.common.model.ApiResponse;
import com.moyo.backend.securityLegacy.jwt.service.JwtReissueServiceLegacy;
import com.moyo.backend.common.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.moyo.backend.common.constant.MoyoConstants.*;

@RestController
@RequiredArgsConstructor
public class JwtReissueControllerLegacy {

    private final JwtReissueServiceLegacy jwtReissueServiceLegacy;
    private final CookieUtils cookieUtils;

    @PostMapping("/auth/reissue/tokenLegacy")
    public ResponseEntity<ApiResponse<?>> reissueJwtTokens(@CookieValue("refresh") String jwtRefreshToken){

        Map<String, String> reIssueTokens = jwtReissueServiceLegacy.reIssueJwt(jwtRefreshToken);

        return ResponseEntity.status(OK)
                .header(SET_COOKIE, cookieUtils.createJwtRefreshTokenCookie(reIssueTokens.get(JWT_REFRESH_TYPE)).toString())
                .body(ApiResponse.success(new JwtReissueResponse(reIssueTokens.get(JWT_ACCESS_TYPE))));
    }
}
