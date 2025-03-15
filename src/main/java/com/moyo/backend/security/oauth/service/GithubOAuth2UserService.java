package com.moyo.backend.security.oauth.service;

import com.moyo.backend.security.oauth.dto.GithubOAuth2User;
import com.moyo.backend.security.oauth.dto.GithubUserProfile;
import com.moyo.backend.security.oauth.dto.UserDto;
import com.moyo.backend.user.User;
import com.moyo.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

        // Default OAuth2 User Service로 깃허브 유저 정보 받아옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // 받아온 정보 중 필요한 데이터 파싱
        GithubUserProfile githubUserProfile = new GithubUserProfile(oAuth2User.getAttributes());
        
        // Id로 이미 가입한 적 있는 회원인지 조회
        User user = userRepository.findById(githubUserProfile.getId()).orElse(null);

        // 회원가입
        if (user == null) {

            log.info("신규 회원 회원 가입 진행, Username : {}", githubUserProfile.getUsername());
            user = User.from(githubUserProfile);
            userRepository.save(user);
        }
        
        // 프로필 업데이트
        else {

            log.info("이미 가입된 회원 Username, ProfileImgUrl 갱신, Username: {}", githubUserProfile.getUsername());
            user.changeUsername(githubUserProfile.getUsername());
            user.changeProfileImgUrl(githubUserProfile.getProfileImgUrl());
        }

        // GithubOAuth2User로 변환
        return new GithubOAuth2User(UserDto.userEntityToUserDto(user));
    }
}
