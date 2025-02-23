package com.moyo.backend.security.oauth.handler;

import com.moyo.backend.common.util.CookieUtils;
import com.moyo.backend.domain.user.Role;
import com.moyo.backend.security.jwt.util.JwtPayloadReader;
import com.moyo.backend.security.jwt.util.JwtProvider;
import com.moyo.backend.security.oauth.dto.GitHubOAuth2User;
import com.moyo.backend.security.oauth.repository.LoginRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.moyo.backend.common.constant.MoyoConstants.SET_COOKIE;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final JwtPayloadReader jwtPayloadReader;
    private final LoginRepository loginRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("로그인 성공 후 JWT 발급 시작");
        GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();

        String providerId = gitHubOAuth2User.getName();

        String jwtRefresh = jwtProvider.createJwtRefresh(providerId);
        loginRepository.save(gitHubOAuth2User.getName(), jwtRefresh, jwtPayloadReader.getExpiration(jwtRefresh));

        response.addHeader(SET_COOKIE,
                CookieUtils.createJwtRefreshCookie(jwtRefresh, jwtPayloadReader.getExpiration(jwtRefresh).getTime()/1000).toString());
        response.sendRedirect("http://localhost:3000/test");
    }
}