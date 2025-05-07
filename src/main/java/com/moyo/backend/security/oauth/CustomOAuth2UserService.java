package com.moyo.backend.security.oauth;

import com.moyo.backend.user.User;
import com.moyo.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        try {
            // DefaultOAuth2User
            OAuth2User oAuth2User = super.loadUser(userRequest);

            GithubOAuth2User githubOAuth2User = GithubOAuth2UserConverter.convert(oAuth2User);

            User user = userRepository.findById(githubOAuth2User.getId()).orElse(null);

            if (user == null) signUp(githubOAuth2User, oAuth2User);
            else updateProfile(user, githubOAuth2User, oAuth2User);

            return githubOAuth2User;

        } catch (DataAccessException dbEx) {
            OAuth2Error oauth2Error = new OAuth2Error("db_error", "DB 접근 중 에러 발생: " + dbEx.getMessage(), null);
            throw new OAuth2AuthenticationException(oauth2Error, dbEx);
        } catch (Exception ex) {
            OAuth2Error oauth2Error = new OAuth2Error("unknown_error","인증 처리 중 알 수 없는 에러 발생: " + ex.getMessage(), null);
            throw new OAuth2AuthenticationException(oauth2Error, ex);
        }
    }


    // 이 부분 트랜잭션은 추후 처리함
    private void signUp(GithubOAuth2User githubOAuth2User, OAuth2User oAuth2User){

        log.info("신규 회원 회원 가입 진행, Username : {}", githubOAuth2User.getUsername());
        User user = User.from(githubOAuth2User, oAuth2User);
        userRepository.save(user);
    }

    private void updateProfile(User user, GithubOAuth2User githubOAuth2User, OAuth2User oAuth2User){

        log.info("이미 가입된 회원 Username, ProfileImgUrl 갱신, Username: {}", githubOAuth2User.getUsername());
        user.changeUsername(githubOAuth2User.getUsername());
        user.changeProfileImgUrl(oAuth2User.getAttribute("avatar_url"));
    }

}
