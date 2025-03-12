package com.moyo.backend.follow.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GitHubUserInfo {

    private String username;
    private String profileImgUrl;

    public GitHubUserInfo changeUsername(String username){
        return new GitHubUserInfo(username, this.profileImgUrl);
    }

    public GitHubUserInfo changeProfileImgUrl(String profileImgUrl){
        return new GitHubUserInfo(this.username, profileImgUrl);
    }
}