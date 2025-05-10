package com.moyo.backend.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyo.backend.common.dto.ApiResponse;
import com.moyo.backend.security.jwt.exception.LoginErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.moyo.backend.common.constant.MoyoConstants.JSON;
import static com.moyo.backend.common.constant.MoyoConstants.UNAUTHORIZED;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,AuthenticationException authException) throws IOException {

        response.setStatus(UNAUTHORIZED);
        response.setContentType(JSON);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(ApiResponse.fail(LoginErrorCode.UNAUTHORIZED_USER.getErrorReason()));
        response.getWriter().write(jsonResponse);
    }
}
