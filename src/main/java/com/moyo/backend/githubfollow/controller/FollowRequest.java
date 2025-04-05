package com.moyo.backend.githubfollow.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowRequest {

    private Long targetId;
    private String targetUsername;
}
