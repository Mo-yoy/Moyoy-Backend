package com.moyo.backend.follow.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 *  맞 팔로우, 상대만 나를 팔로우, 나만 상대를 팔로우한 리스트 반환 API 스펙이 현재 모두 동일함.
 *  추후 변경될 거 같지도 않고 성격이 비슷한 응답들 이라 우선은 함께 묶어서 처리.
 */

@Getter
@AllArgsConstructor
public class FollowListResponse {

    private List<GithubUserDto> githubUserList;
    private boolean lastPage;
}