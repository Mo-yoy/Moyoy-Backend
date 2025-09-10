package com.moyoy.api.ranking.presentation.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RankingListRequest(
	@NotBlank @Pattern(regexp = "week|month|year") String period,

	@Min(0) Integer page,

	@Min(1) @Max(30) Integer size) {

	public RankingListRequest {
		if (page == null)
			page = 0;
		if (size == null)
			size = 20;
	}
}
