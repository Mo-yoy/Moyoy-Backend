package com.moyo.backend.security.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GithubOAuth2UserConverter {

    private static final String GITHUB_OAUTH2_USER_ID = "id";

    public static GithubOAuth2User convert(OAuth2User oAuth2User) {

        // 권한 추출
        Set<GrantedAuthority> authorities = new HashSet<>();
        oAuth2User.getAuthorities().forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getAuthority())));

        // GitHub 사용자 정보 중 Principal에 유지할 정보 추출
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", oAuth2User.getAttribute(GITHUB_OAUTH2_USER_ID));

        // GitHubOAuth2User 객체 생성 및 반환
        return new GithubOAuth2User(authorities, attributes);
    }
}