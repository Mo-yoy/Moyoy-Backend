package com.moyo.backend.security.oauth.handler;

import com.moyo.backend.common.util.CookieFactory;
import com.moyo.backend.security.jwt.util.JwtPayloadReader;
import com.moyo.backend.security.jwt.util.JwtProvider;
import com.moyo.backend.security.oauth.dto.GitHubOAuth2User;
import com.moyo.backend.security.oauth.repository.LoginRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CookieFactory cookieFactory;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("로그인 성공 후 JWT 발급 시작");
        GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();

        String providerId = gitHubOAuth2User.getName();
        String role = String.valueOf(gitHubOAuth2User.getAuthorities().stream().findFirst().orElseThrow(RuntimeException::new));

        String jwtRefresh = jwtProvider.createJwtRefresh(providerId,role);
        loginRepository.save(providerId, jwtRefresh, jwtPayloadReader.getExpiration(jwtRefresh));

        response.addHeader(SET_COOKIE, cookieFactory.createJwtRefreshCookie(jwtRefresh).toString());
        response.sendRedirect("http://localhost:3000/test");
    }
}