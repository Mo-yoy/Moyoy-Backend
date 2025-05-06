package com.moyo.backend.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.moyo.backend.common.constant.MoyoConstants.UNAUTHORIZED;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        log.error("OAuth 인증 필터 에서 인증 처리 중 예외 발생 : {}",exception.getMessage());

        // JSON 형식으로 실패 메시지 반환
        String errorResponse = "{"
                + "\"status\": 401,"
                + "\"code\": \"LOGIN_401_1\","
                + "\"message\": \"깃허브로 인증 중 에러 발생\","
                + "\"data\": null"
                + "}";

        response.getWriter().write(errorResponse);
    }
}
