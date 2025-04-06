package com.moyo.backend.security.jwt.controller;


import com.moyo.backend.common.model.ApiResponse;
import com.moyo.backend.security.jwt.service.JwtReissueService;
import com.moyo.backend.common.util.CookieFactory;
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

    private final JwtReissueService jwtReissueService;
    private final CookieFactory cookieFactory;

    @PostMapping("/auth/reissue/token")
    public ResponseEntity<ApiResponse<?>> reissueJwtTokens(@CookieValue("refresh") String jwtRefreshToken){

        Map<String, String> reIssueTokens = jwtReissueService.reIssueJwt(jwtRefreshToken);

        return ResponseEntity.status(OK)
                .header(SET_COOKIE, cookieFactory.createJwtRefreshCookie(reIssueTokens.get(REFRESH_TYPE)).toString())
                .body(ApiResponse.success(new JwtReissueResponse(reIssueTokens.get(ACCESS_TYPE))));
    }
}
