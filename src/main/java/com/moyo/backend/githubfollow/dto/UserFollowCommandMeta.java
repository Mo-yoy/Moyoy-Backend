package com.moyo.backend.githubfollow.dto;

import lombok.Builder;
import lombok.Getter;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_MIN_REQUEST_THRESHOLD;

@Getter
public class UserFollowCommandMeta {

    private final Integer rateLimitRemaining;
    private final Long targetUserId;
    private final String targetUsername;
    private final String targetUserProfileImgUrl;

    @Builder
    private UserFollowCommandMeta(Integer rateLimitRemaining, Long targetUserId, String targetUsername, String targetUserProfileImgUrl) {
        this.targetUserId = targetUserId;
        this.targetUsername = targetUsername;
        this.targetUserProfileImgUrl = targetUserProfileImgUrl;

        if (rateLimitRemaining < GITHUB_MIN_REQUEST_THRESHOLD)
            throw new RuntimeException("깃허브에 너무 많은 요청을 보내고 있습니다.");

        this.rateLimitRemaining = rateLimitRemaining;
    }

    // 아이디는 그대로인데 이름이 변경된 경우
    public boolean isTargetUserNameChanged(Long requestUserId, String requestUsername) {

        return targetUserId.equals(requestUserId) && !targetUsername.equals(requestUsername);
    }

    // 요청 수행이 불가능 함. username을 통해 얻어온 Id가 달라서 완전히 다른 유저인 경우
    public boolean isInvalidRequest(Long requestUserId){
        return !targetUserId.equals(requestUserId);
    }



}
