package com.moyo.backend.githubfollow.dto;

import com.moyo.backend.githubfollow.model.FollowUser;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FollowDetectResponse {

    private List<FollowUser> userList;
    private boolean lastPage;
}