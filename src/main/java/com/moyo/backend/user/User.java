package com.moyo.backend.user;

import com.moyo.backend.security.oauth.dto.GithubUserProfile;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

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

    public static User from(GithubUserProfile githubUserProfile){
        return User.builder()
                .id(githubUserProfile.getId())
                .username(githubUserProfile.getUsername())
                .profileImgUrl(githubUserProfile.getProfileImgUrl())
                .role(Role.USER)
                .build();
    }

    public void changeUsername(String username){
        this.username = username;
    }

    public void changeProfileImgUrl(String profileImgUrl){
        this.profileImgUrl = profileImgUrl;
    }
}

