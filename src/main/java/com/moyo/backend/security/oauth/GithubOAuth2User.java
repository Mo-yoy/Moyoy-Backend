package com.moyo.backend.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class GithubOAuth2User implements OAuth2User {

    private final Set<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    // OAuth Authorized Client Id (String)
    @Override
    public String getName() {
        return String.valueOf(attributes.get("id"));
    }

    public Long getId() {
        return Long.parseLong(attributes.get("id").toString());
    }
}
