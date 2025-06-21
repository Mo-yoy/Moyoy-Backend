package com.moyo.backend.domain.github_ranking.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.moyo.backend.domain.github_ranking.implement.RankingDuration;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class RankingSearchRequest {

	@NotNull(message = "Ranking Duration Type이 존재하지 않습니다")
	private RankingDuration duration;

	int page = 0;
	int size = 20;
}
