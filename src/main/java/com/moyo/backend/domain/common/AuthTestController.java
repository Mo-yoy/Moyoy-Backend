package com.moyo.backend.domain.common;

import com.moyo.backend.security.oauth.dto.GitHubOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.moyo.backend.common.constant.MoyoConstants.ANONYMOUS_USER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthTestController {

    private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @GetMapping("/auth/only/test")
    public String authOnly(Authentication authentication, @AuthenticationPrincipal GitHubOAuth2User githubOAuth2User){

        log.info("인증 된 사용자만 접근 할 수 있는 Security Test 진행");

        loggingAuthenticate();
        loggingLoginUserInfo((OAuth2AuthenticationToken) authentication, githubOAuth2User);

        return "OK";
    }



    @GetMapping("/permit/all/test")
    public String permitAll(Authentication authentication, @AuthenticationPrincipal GitHubOAuth2User githubOAuth2User) {

        log.info("모두가 접근 가능한 Security Test 진행");
        
        loggingAuthenticate();

        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(ANONYMOUS_USER)) log.info("Guest 유저 식별");
        else loggingLoginUserInfo((OAuth2AuthenticationToken) authentication, githubOAuth2User);

        return "OK";
    }

    private void loggingAuthenticate() {
        log.info("SecurityContextHolder.getContext() = {}" , SecurityContextHolder.getContext());
        log.info("SecurityContextHolder.getContext().getAuthentication() = {}", SecurityContextHolder.getContext().getAuthentication());
        log.info("SecurityContextHolder.getContext().getAuthentication().getPrincipal() = {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        log.info("SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass() = {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass());
    }

    private void loggingLoginUserInfo(OAuth2AuthenticationToken authentication, GitHubOAuth2User githubOAuth2User) {
        OAuth2AuthenticationToken authenticationToken = authentication;
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
    }
}
