package com.moyo.backend.common.security.oauth.handler;

import com.moyo.backend.common.security.jwt.util.JwtPayloadReader;
import com.moyo.backend.common.security.jwt.util.JwtProvider;
import com.moyo.backend.common.security.oauth.repository.LoginRepository;
import com.moyo.backend.common.util.CookieFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.moyo.backend.common.constant.MoyoConstants.SET_COOKIE;

@Slf4j
@Component
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final JwtPayloadReader jwtPayloadReader;
    private final LoginRepository loginRepository;
    private final CookieFactory cookieFactory;
    private final String frontLoginSuccessURI;

    public OAuthLoginSuccessHandler(JwtProvider jwtProvider,
                                    JwtPayloadReader jwtPayloadReader,
                                    LoginRepository loginRepository,
                                    CookieFactory cookieFactory,
                                    @Value("${spring.login.default-uri}") String frontLoginSuccessURI) {
        this.jwtProvider = jwtProvider;
        this.jwtPayloadReader = jwtPayloadReader;
        this.loginRepository = loginRepository;
        this.cookieFactory = cookieFactory;
        this.frontLoginSuccessURI = frontLoginSuccessURI;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("로그인 성공 후 JWT 발급 시작");
        GithubOAuth2User gitHubOAuth2User = (GithubOAuth2User) authentication.getPrincipal();

        String providerId = gitHubOAuth2User.getName();
        String role = String.valueOf(gitHubOAuth2User.getAuthorities().stream().findFirst().orElseThrow(RuntimeException::new));

        String jwtRefresh = jwtProvider.createJwtRefresh(providerId,role);
        String expiredJwtAccess = jwtProvider.createExpiredJwtAccess();

        loginRepository.save(providerId, jwtRefresh, jwtPayloadReader.getExpiration(jwtRefresh));

        response.addHeader(SET_COOKIE, cookieFactory.createJwtRefreshCookie(jwtRefresh).toString());
        response.addHeader(SET_COOKIE, cookieFactory.createExpiredAccessTokenCookie(expiredJwtAccess).toString());
        response.sendRedirect(frontLoginSuccessURI);

    }
}