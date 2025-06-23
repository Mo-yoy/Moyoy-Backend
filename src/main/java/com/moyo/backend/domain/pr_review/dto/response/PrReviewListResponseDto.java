package com.moyo.backend.domain.pr_review.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyo.backend.domain.pr_review.dto.PrReviewDto;

@Getter
@AllArgsConstructor
public class PrReviewListResponseDto {

	List<PrReviewDto> prReviewList;

	private Boolean isLast;

	public static PrReviewListResponseDto of(List<PrReviewDto> prReviewList, Boolean isLast) {
		return new PrReviewListResponseDto(prReviewList, isLast);
	}
}
