package com.moyo.backend.security.oauth.handler;

import com.moyo.backend.security.jwt.util.JwtPayloadReader;
import com.moyo.backend.security.jwt.util.JwtProvider;
import com.moyo.backend.security.oauth.dto.GithubOAuth2User;
import com.moyo.backend.security.oauth.repository.LoginRepository;
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

        GithubOAuth2User gitHubOAuth2User = (GithubOAuth2User) authentication.getPrincipal();

        Long userId = gitHubOAuth2User.getId();
        String role = String.valueOf(gitHubOAuth2User.getAuthorities().stream().findFirst().orElseThrow(RuntimeException::new));

        log.info("Id : {} 회원 로그인 성공 후 JWT 발급 시작", userId);

        String jwtRefresh = jwtProvider.createJwtRefresh(userId,role);

        loginRepository.save(userId, jwtRefresh, jwtPayloadReader.getExpiration(jwtRefresh));

        response.addHeader(SET_COOKIE, cookieFactory.createJwtRefreshCookie(jwtRefresh).toString());
        response.sendRedirect(frontLoginSuccessURI);
    }
}