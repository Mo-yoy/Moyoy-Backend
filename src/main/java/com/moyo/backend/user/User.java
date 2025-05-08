package com.moyo.backend.user;

import com.moyo.backend.common.entity.BaseTimeEntity;
import com.moyo.backend.security.oauth.GithubOAuth2User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    // 깃허브와 연동
    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String profileImgUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private User(Long id, String username, String profileImgUrl){
        this.id = id;
        this.username = username;
        this.profileImgUrl = profileImgUrl;
    }

    public static User from(OAuth2User oAuth2User){
        return User.builder()
                .id(Long.parseLong(oAuth2User.getAttribute("id").toString()))
                .username(oAuth2User.getAttribute("login"))
                .profileImgUrl(oAuth2User.getAttribute("avatar_url"))
                .build();
    }

    public void changeUsername(String username){
        this.username = username;
    }

    public void changeProfileImgUrl(String profileImgUrl){
        this.profileImgUrl = profileImgUrl;
    }

}

