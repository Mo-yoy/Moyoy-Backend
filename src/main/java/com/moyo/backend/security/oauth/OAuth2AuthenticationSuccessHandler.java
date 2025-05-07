package com.moyo.backend.security.oauth;

import com.moyo.backend.common.util.CookieUtils;
import com.moyo.backend.security.jwt.MacSecuritySigner;
import com.moyo.backend.security.jwt.domain.JwtWhiteList;
import com.moyo.backend.security.jwt.repository.JwtWhiteListRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;

import static com.moyo.backend.common.constant.MoyoConstants.JWT_REFRESH_TYPE;
import static com.moyo.backend.common.constant.MoyoConstants.SET_COOKIE;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CookieUtils cookieUtils;
    private final OctetSequenceKey jwk;
    private final MacSecuritySigner macSecuritySigner;
    private final JwtWhiteListRepository jwtWhiteListRepository;
    private final String frontLoginSuccessURI;

    public OAuth2AuthenticationSuccessHandler(CookieUtils cookieUtils, OctetSequenceKey jwk, MacSecuritySigner macSecuritySigner,
                                              JwtWhiteListRepository jwtWhiteListRepository, @Value("${spring.login.default-uri}") String frontLoginSuccessURI) {

        this.cookieUtils = cookieUtils;
        this.jwk = jwk;
        this.macSecuritySigner = macSecuritySigner;
        this.jwtWhiteListRepository = jwtWhiteListRepository;
        this.frontLoginSuccessURI = frontLoginSuccessURI;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String jwtRefreshToken;
        GithubOAuth2User userPrincipal = (GithubOAuth2User) authentication.getPrincipal();

        try {
            jwtRefreshToken = macSecuritySigner.getJwtToken(userPrincipal, jwk, JWT_REFRESH_TYPE);
            response.addHeader(SET_COOKIE, cookieUtils.createJwtRefreshTokenCookie(jwtRefreshToken).toString());
            log.info("Id : {} 회원 로그인 성공 후 JWT 발급", userPrincipal.getId());

            SignedJWT signedJWT = SignedJWT.parse(jwtRefreshToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            jwtWhiteListRepository.save(JwtWhiteList.from(claimsSet, jwtRefreshToken));

            // 스프린트 1의 요구사항 에서는 인증 후 사용자의 이전 요청이 있었을 때에 대한 후 처리를 진행 하지 않고
            // 무조건 default URI로 리다이렉트 함.
            response.sendRedirect(frontLoginSuccessURI);
        }
        catch (JOSEException | DataAccessException | ParseException ex){
            throw new RuntimeException(ex);
        }
    }
}
