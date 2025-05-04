package com.moyo.backend.user;

import com.moyo.backend.common.entity.BaseTimeEntity;
import com.moyo.backend.security.oauth.GithubOAuth2User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder(access = AccessLevel.PRIVATE)
    private User(Long id, String username, String profileImgUrl, Role role){
        this.id = id;
        this.username = username;
        this.profileImgUrl = profileImgUrl;
        this.role = role;
    }

    public static User from(GithubOAuth2User githubOAuth2User){
        return User.builder()
                .id(githubOAuth2User.getId())
                .username(githubOAuth2User.getUsername())
                .profileImgUrl(githubOAuth2User.getProfileImageUrl())
                .build();
    }

    public void changeUsername(String username){
        this.username = username;
    }

    public void changeProfileImgUrl(String profileImgUrl){
        this.profileImgUrl = profileImgUrl;
    }

}

