package com.moyo.backend.securityLegacy.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyo.backend.common.exception.ErrorReason;
import com.moyo.backend.common.model.ApiResponse;
import com.moyo.backend.securityLegacy.jwt.exception.LoginErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.moyo.backend.common.constant.MoyoConstants.JSON;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationExceptionHandleFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 임시
        try{
            filterChain.doFilter(request,response);
        }
        catch (ExpiredJwtException ex){

            createErrorResponse(response, LoginErrorCode.JWT_EXPIRED.getErrorReason());
        }
    }

    private void createErrorResponse(HttpServletResponse httpServletResponse, ErrorReason errorReason) throws IOException {

        httpServletResponse.setStatus(errorReason.getStatus());
        httpServletResponse.setContentType(JSON);
        httpServletResponse.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(ApiResponse.fail(errorReason));
        httpServletResponse.getWriter().write(jsonResponse);
    }
}

