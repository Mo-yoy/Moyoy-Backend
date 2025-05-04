package com.moyo.backend.securityLegacy.oauth.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GithubUserProfile {

    private static final String GITHUB_USER_ID = "id";
    private static final String GITHUB_USER_PROFILE_IMAGE_URL = "avatar_url";
    private static final String GITHUB_USER_TAG = "login";


    private final Map<String, Object> attributes;

    public Long getId() {

        Object userId = attributes.get(GITHUB_USER_ID);

        if(userId instanceof Integer) return ((Integer) userId).longValue();
        else return null;
    }

    public String getProfileImgUrl() {

        return attributes.get(GITHUB_USER_PROFILE_IMAGE_URL).toString();
    }

    public String getUsername() {

        return attributes.get(GITHUB_USER_TAG).toString();
    }
}
