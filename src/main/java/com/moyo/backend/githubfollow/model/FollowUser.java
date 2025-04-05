package com.moyo.backend.githubfollow.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class FollowUser {

    private Long id;
    private String username;
    private String profileImgUrl;

    public void changeUsername(String username){
        this.username = username;
    }
}