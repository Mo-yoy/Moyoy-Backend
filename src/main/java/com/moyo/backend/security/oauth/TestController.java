package com.moyo.backend.security.oauth;

import com.moyo.backend.common.constant.MoyoConstants;
import com.moyo.backend.security.jwt.JwtReissueService;
import com.moyo.backend.security.oauth.dto.GitHubOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.moyo.backend.common.constant.MoyoConstants.JWT_PAYLOAD_REFRESH_TYPE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final JwtReissueService jwtReissueService;

    @GetMapping("/api/auth/test")
    public String test(Authentication authentication, @AuthenticationPrincipal GitHubOAuth2User githubOAuth2User){

        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = authenticationToken.getPrincipal();
        GitHubOAuth2User oAuth2User1 = (GitHubOAuth2User) oAuth2User;

        log.info("Authentication 으로 로그 찍어보기 : {}", oAuth2User1.getName());

        log.info("@AuthenticationPrincipal로 로그 찍어보기 : {} ", githubOAuth2User.getName());

        log.info("인증 테스트 실행");

        log.info("===== default OAuth2AuthorizedClientRepository 구현체 조회 =====");
        log.info(String.valueOf(oAuth2AuthorizedClientRepository.getClass()));

        log.info("===== default OAuth2AuthorizedClientService 구현체 조회 =====");
        log.info(String.valueOf(oAuth2AuthorizedClientService.getClass()));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("===== SecurityContextHolder에서 Authentication.getPrincipal 조회 =====");
        log.info(String.valueOf(auth.getPrincipal().getClass()));

        GitHubOAuth2User userPrincipal = (GitHubOAuth2User) auth.getPrincipal();
        log.info("===== SecurityContextHolder에서 Authentication.getPrincipal에 넣어둔 GithubOAuth2User 조회 =====");

        log.info("ProviderId : {}", userPrincipal.getName());

        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient("github", userPrincipal.getName());
        log.info("===== OAuth2AuthorizedClient 조회 : {}  =====", oAuth2AuthorizedClient);

        log.info("===== 현재 로그인 한 사용자의 OAuth2 Access Token 조회 : {} =====", oAuth2AuthorizedClient.getAccessToken().getTokenValue());

        return "OK";
    }

    @PostMapping("/auth/reissue/token")
    public ResponseEntity<Void> reissueJwtTokens(@CookieValue("refresh") String jwtRefreshToken){

        log.info("토큰 까봄 : {}", jwtRefreshToken.substring(0,6));

        Map<String, String> reIssueTokens = jwtReissueService.reIssueJwt(jwtRefreshToken);

        return ResponseEntity.status(200)

                .header("Authorization", "Bearer " + reIssueTokens.get("access"))
                .header("Set-Cookie", JWT_PAYLOAD_REFRESH_TYPE + "=" + reIssueTokens.get("refresh") + "; Path=/; Max-Age=600; SameSite=Lax; HttpOnly; Secure;")
                .build();
    }

}
