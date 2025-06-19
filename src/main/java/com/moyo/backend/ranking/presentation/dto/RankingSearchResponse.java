package com.moyo.backend.ranking.presentation.dto;

import com.moyo.backend.ranking.implement.RankingDuration;
import com.moyo.backend.user.domain.User;
import org.springframework.data.domain.Slice;

import java.util.List;

public record RankingSearchResponse(
        List<UserRankingDetails> userList,
        boolean lastPage
) {

    public static RankingSearchResponse from (Slice<User> users, RankingDuration duration){

        List<UserRankingDetails> userList = users.getContent().stream()
                .map(user -> UserRankingDetails.from(user, duration))
                .toList();

        return new RankingSearchResponse(userList, users.isLast());
    }
}