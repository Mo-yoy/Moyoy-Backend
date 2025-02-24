package com.moyo.backend.security.oauth.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class GitHubOAuth2User implements OAuth2User {

    private final UserDto userDto;

    @Override
    public  <A> A getAttribute(String name) {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(userDto.getRole().getValue()));
    }


    /**
     *  추후 OAuth2 토큰이 필요할 때, 스프링 MVC 에서 OAuth2AuthorizedClientService를 사용 하게 된다면
     *  loadAuthorizedClient(String clientRegistrationId, String principalName)을 호출 하게 됩니다.
     *  이 때, principalName 으로 사용될 식별자에 우리 서비스의 사용자별로 깃허브 OAuth앱 사이에 고유한 값으로
     *  저장되는 ProviderId가 적합 하다 생각 했습니다.
     */
    @Override
    public String getName() {
        return userDto.getProviderId();
    }
}
