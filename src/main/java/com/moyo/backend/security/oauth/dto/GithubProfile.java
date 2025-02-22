package com.moyo.backend.security.oauth.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GithubProfile {

    private final Map<String, Object> attributes;
    public String getProviderId() {

        return attributes.get("id").toString();
    }

    public String getProfileImgUrl() {

        return attributes.get("avatar_url").toString();
    }

    public String getUsername() {

        return attributes.get("login").toString();
    }
}
