package com.moyo.common.annotation;

import com.moyo.backend.security.oauth.GithubOAuth2User;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.Map;

public class WithMockGithubOAuth2UserSecurityContextFactory implements WithSecurityContextFactory<WithMockGithubOAuth2User> {
    @Override
    public SecurityContext createSecurityContext(WithMockGithubOAuth2User annotation) {
        Map<String, Object> attributes = Map.of("id", annotation.id());
        GithubOAuth2User principal = new GithubOAuth2User(Collections.emptySet(), attributes);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
