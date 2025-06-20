package com.moyo.backend.pr_review.dto.response;

import java.util.List;

import com.moyo.backend.pr_review.dto.PrReviewDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrReviewListResponseDto {

	List<PrReviewDto> prReviewList;

	private Boolean isLast;

	public static PrReviewListResponseDto of(List<PrReviewDto> prReviewList, Boolean isLast) {
		return new PrReviewListResponseDto(prReviewList, isLast);
	}
}
