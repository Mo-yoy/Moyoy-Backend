package com.moyo.backend.githubFollow.dto;

import com.moyo.backend.common.util.TimeSinceFormatter;
import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GithubFollowDetectResponse {

    private List<GithubFollowUserDto> userList;
    private boolean lastPage;
    private int totalUserCount;
    private String lastSyncAt;

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    static class GithubFollowUserDto {
        private Long id;
        private String username;
        private String avatarUrl;

        public static GithubFollowUserDto from(GithubFollowUser user) {
            return GithubFollowUserDto.builder()
                    .id(user.id())
                    .username(user.username())
                    .avatarUrl(user.profileImgUrl())
                    .build();
        }
    }


    public static GithubFollowDetectResponse of(Slice<GithubFollowUser> pagedSlice, LocalDateTime createdAt, int totalUserCount) {

        List<GithubFollowUserDto> userDtoList = pagedSlice.getContent().stream()
                .map(GithubFollowUserDto::from)
                .toList();

        return GithubFollowDetectResponse.builder()
                .userList(userDtoList)
                .lastPage(pagedSlice.isLast())
                .totalUserCount(totalUserCount)
                .lastSyncAt(TimeSinceFormatter.formatTimeSince(createdAt))
                .build();
    }
}