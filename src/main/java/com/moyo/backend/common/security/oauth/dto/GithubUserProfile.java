package com.moyo.backend.common.security.oauth.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GithubUserProfile {

    private static final String GITHUB_OAUTH_APP_USER_ID = "id";
    private static final String GITHUB_USER_PROFILE_IMAGE_URL = "avatar_url";
    private static final String GITHUB_USER_TAG = "login";


    private final Map<String, Object> attributes;

    public String getProviderId() {

        return attributes.get(GITHUB_OAUTH_APP_USER_ID).toString();
    }

    public String getProfileImgUrl() {

        return attributes.get(GITHUB_USER_PROFILE_IMAGE_URL).toString();
    }

    public String getUsername() {

        return attributes.get(GITHUB_USER_TAG).toString();
    }
}
