package com.moyo.backend.domain.user;

import com.moyo.backend.security.oauth.dto.GithubUserProfile;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String profileImgUrl;

    @Column(unique = true)
    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String username, String profileImgUrl, String providerId, Role role){
        this.username = username;
        this.profileImgUrl = profileImgUrl;
        this.providerId = providerId;
        this.role = role;
    }

    public static User fromGithubProfile(GithubUserProfile githubUserProfile){
        return User.builder()
                .username(githubUserProfile.getUsername())
                .profileImgUrl(githubUserProfile.getProfileImgUrl())
                .providerId(githubUserProfile.getProviderId())
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

