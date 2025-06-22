package com.moyo.backend.common.presentation;

import static com.moyo.backend.common.constant.MoyoConstants.ANONYMOUS_USER;

import java.util.StringTokenizer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyo.backend.domain.auth.oauth.GithubOAuth2User;

@Deprecated
@Slf4j
@Profile({"local", "test"})
@RestController
@RequiredArgsConstructor
public class AuthTestController {

	private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
	private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

	@GetMapping("/auth/only/test")
	public String authOnly(
		Authentication authentication,
		@AuthenticationPrincipal GithubOAuth2User githubOAuth2User) {

		log.info("인증 된 사용자만 접근 할 수 있는 Security Test 진행");

		loggingAuthenticate(githubOAuth2User);
		loggingLoginUserInfo((OAuth2AuthenticationToken)authentication, githubOAuth2User);

		return "OK";
	}

	@GetMapping("/permit/all/test")
	public String permitAll(
		Authentication authentication,
		@AuthenticationPrincipal GithubOAuth2User githubOAuth2User,
		@PageableDefault(page = 0, size = 15, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {

		log.info("모두가 접근 가능한 Security Test 진행");

		loggingPageable(pageable);
		loggingAuthenticate(githubOAuth2User);

		System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(ANONYMOUS_USER))
			log.info("Guest 유저 식별");
		else
			loggingLoginUserInfo((OAuth2AuthenticationToken)authentication, githubOAuth2User);

		return "OK";
	}

	@GetMapping("/auth/test/admin")
	public String adminTest(@AuthenticationPrincipal GithubOAuth2User githubOAuth2User) {
		log.info("권한 : {} ", githubOAuth2User.getAuthorities());

		return "OK";
	}

	private void loggingPageable(Pageable pageable) {
		log.info("pageable.getPageNumber() : {} ", pageable.getPageNumber());
		log.info("pageable.getSort() : {}", pageable.getSort());
		log.info("pageable.getPageSize() : {}", pageable.getPageSize());

		StringTokenizer stk = new StringTokenizer(pageable.getSort().toString(), ",");
		while (stk.hasMoreTokens()) {
			String cur = stk.nextToken();
			int colon = cur.indexOf(":");

			if (colon == -1)
				System.out.println("z");
			else if (colon == 0)
				System.out.println("큰일남 :으로 시작함");
			else
				System.out.println(cur.substring(0, colon));
		}

	}

	private void loggingAuthenticate(GithubOAuth2User githubOAuth2User) {

		log.info("githubOAuth2User==null = {}", githubOAuth2User == null);
		log.info("SecurityContextHolder.getContext() = {}", SecurityContextHolder.getContext());
		log.info("SecurityContextHolder.getContext().getAuthentication() = {}", SecurityContextHolder.getContext().getAuthentication());
		log.info("SecurityContextHolder.getContext().getAuthentication().getPrincipal() = {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		log.info("SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass() = {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass());
	}

	private void loggingLoginUserInfo(
		OAuth2AuthenticationToken authentication,
		GithubOAuth2User githubOAuth2User) {
		OAuth2User oAuth2User = authentication.getPrincipal();
		GithubOAuth2User oAuth2User1 = (GithubOAuth2User)oAuth2User;

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

		GithubOAuth2User userPrincipal = (GithubOAuth2User)auth.getPrincipal();
		log.info("===== SecurityContextHolder에서 Authentication.getPrincipal에 넣어둔 GithubOAuth2User 조회 =====");

		log.info("userId : {}", userPrincipal.getName());

		OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient("github", userPrincipal.getName());
		log.info("===== OAuth2AuthorizedClient 조회 : {}  =====", oAuth2AuthorizedClient);

		log.info("===== 현재 로그인 한 사용자의 OAuth2 Access Token 조회 : {} =====", oAuth2AuthorizedClient.getAccessToken().getTokenValue());

		OAuth2AuthorizedClient clientInfo = oAuth2AuthorizedClientService.loadAuthorizedClient("github", userPrincipal.getName());

		OAuth2AccessToken accessToken = clientInfo.getAccessToken();
		OAuth2RefreshToken refreshToken = clientInfo.getRefreshToken();

		log.info("OAuth Access : {}", accessToken);
		log.info("OAuth Refresh : {}", refreshToken);

		log.info("OAuth Access Info : type = {}, scope = {}, issuedAt = {}, expiresin = {}", accessToken.getTokenType(), accessToken.getScopes(), accessToken.getIssuedAt(),
			accessToken.getExpiresAt());
		//        log.info("OAuth Refresh Info : value = {}, issuedAt = {}, expiresin = {}", refreshToken.getTokenValue(), refreshToken.getIssuedAt(), refreshToken.getExpiresAt());
	}
}
