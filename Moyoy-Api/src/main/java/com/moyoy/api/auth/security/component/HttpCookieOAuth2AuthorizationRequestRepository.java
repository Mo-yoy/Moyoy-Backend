package com.moyoy.api.auth.security.component;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import com.moyoy.api.common.util.CookieUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *   OAuth Authorization Code Grant 에서 사용자가 실제 OAuth Provider 에서 로그인을 성공 후
 *   Code를 들고 왔을 때, 해당 Code 만을 탈취하여 기존 사용자인척 하는 공격을 방어
 *
 *   다중 서버 환경에서 세션 Storage를 사용하고 있지 않아서 이를 쿠키로 돌림
 */

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_authorization_request";

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
			.map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
			.orElse(null);
	}

	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
		if (authorizationRequest == null) {
			CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
			return;
		}

		CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest));
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
		OAuth2AuthorizationRequest authorizationRequest = this.loadAuthorizationRequest(request);

		if (authorizationRequest != null) {
			CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
		}

		return authorizationRequest;
	}

}
