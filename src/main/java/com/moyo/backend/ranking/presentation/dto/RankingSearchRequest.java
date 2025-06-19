package com.moyo.backend.ranking.presentation.dto;

import com.moyo.backend.ranking.implement.RankingDuration;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RankingSearchRequest {

    @NotNull(message = "Ranking Duration Type이 존재하지 않습니다")
    private RankingDuration duration;

    int page = 0;
    int size = 20;
}