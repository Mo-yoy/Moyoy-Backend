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
    private static final String GITHUB_OAUTH2_USER_PROFILE_IMAGE_URL = "avatar_url";
    private static final String GITHUB_OAUTH2_USER_TAG = "login";


    public static GithubOAuth2User convert(OAuth2User oAuth2User) {

        // 권한 추출
        Set<GrantedAuthority> authorities = new HashSet<>();
        oAuth2User.getAuthorities().forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getAuthority())));

        // GitHub 에서 반환된 속성들
        Map<String, Object> attributes = new HashMap<>();

        // GitHub 사용자 정보 추출
        attributes.put("id", oAuth2User.getAttribute(GITHUB_OAUTH2_USER_ID));
        attributes.put("profile_img_url", oAuth2User.getAttribute(GITHUB_OAUTH2_USER_PROFILE_IMAGE_URL));
        attributes.put("username", oAuth2User.getAttribute(GITHUB_OAUTH2_USER_TAG));

        // GitHubOAuth2User 객체 생성 및 반환
        return new GithubOAuth2User(authorities, attributes);
    }
}