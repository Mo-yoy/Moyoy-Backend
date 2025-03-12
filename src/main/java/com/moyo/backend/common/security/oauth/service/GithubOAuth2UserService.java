package com.moyo.backend.common.security.oauth.service;


import com.moyo.backend.common.security.oauth.dto.GithubUserProfile;
import com.moyo.backend.follow.domain.entity.Follower;
import com.moyo.backend.follow.domain.entity.Following;
import com.moyo.backend.follow.domain.entity.GithubUser;
import com.moyo.backend.follow.infrastructure.httpClient.FollowHttpClientLegacy;
import com.moyo.backend.follow.infrastructure.httpClient.dto.GithubUserResponse;
import com.moyo.backend.follow.infrastructure.repository.FollowerJpaRepository;
import com.moyo.backend.follow.infrastructure.repository.FollowingJpaRepository;
import com.moyo.backend.follow.infrastructure.repository.GithubUserJpaRepository;
import com.moyo.backend.user.User;
import com.moyo.backend.user.UserRepository;
import com.moyo.backend.common.security.oauth.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  우리 서비스는 내부 로직의 거의 모든 부분 에서 GitHub OAuth App Access Token을 이용한 API가 필요 합니다.
 *  추후 다른 OAuth Provider를 추가할 계획이 없습니다.
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GithubOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        GithubUserProfile githubUserProfile = new GithubUserProfile(oAuth2User.getAttributes());

        User user = userRepository.findByProviderId(githubUserProfile.getProviderId());

        // 회원가입, 프로필 업데이트
        if (user == null) {

            log.info("신규 회원 회원 가입 진행, Username : {}", githubUserProfile.getUsername());
            user = User.from(githubUserProfile);
            userRepository.save(user);
        }
        else {

            log.info("이미 가입된 회원 Username, ProfileImgUrl 갱신, Username: {}", githubUserProfile.getUsername());
            user.changeUsername(githubUserProfile.getUsername());
            user.changeProfileImgUrl(githubUserProfile.getProfileImgUrl());
        }

        return new GithubOAuth2User(UserDto.from(user));
    }
}
