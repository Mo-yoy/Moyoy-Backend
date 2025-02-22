package com.moyo.backend.domain.user;

import com.moyo.backend.security.oauth.dto.GithubProfile;
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

    private String providerId;

    private String role;

    @Builder
    public User(String username, String profileImgUrl, String providerId, String role){
        this.username = username;
        this.profileImgUrl = profileImgUrl;
        this.providerId = providerId;
        this.role = role;
    }

    public static User fromGithubProfile(GithubProfile githubProfile){
        return User.builder()
                .username(githubProfile.getUsername())
                .profileImgUrl(githubProfile.getProfileImgUrl())
                .providerId(githubProfile.getProviderId())
                .role("ROLE_USER")
                .build();
    }

    public void changeUsername(String username){
        this.username = username;
    }

    public void changeProfileImgUrl(String profileImgUrl){
        this.profileImgUrl = profileImgUrl;
    }
}

