package com.moyo.backend.ranking.presentation.dto;

import com.moyo.backend.ranking.implement.RankingDuration;
import com.moyo.backend.user.domain.User;

public record UserRankingDetails(
        String profileImageUrl,
        String username,
        long rankPoint
) {

    public static UserRankingDetails from(User user, RankingDuration duration) {

        return switch (duration) {
            case WEEK ->
                    new UserRankingDetails(user.getProfileImgUrl(), user.getUsername(), user.getRanking().getWeeklyPoint());
            case MONTH ->
                    new UserRankingDetails(user.getProfileImgUrl(), user.getUsername(), user.getRanking().getMonthlyPoint());
            case YEAR ->
                    new UserRankingDetails(user.getProfileImgUrl(), user.getUsername(), user.getRanking().getYearlyPoint());
        };
    }
}