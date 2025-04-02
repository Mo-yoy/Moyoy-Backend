package com.moyo.backend.pr_review.dto.response;

import com.moyo.backend.pr_review.dto.PrReviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PrReviewListResponseDto {

    List<PrReviewDto> prReviewList;

    private Boolean isLast;

    public static PrReviewListResponseDto of(List<PrReviewDto> prReviewList, Boolean isLast) {
        return new PrReviewListResponseDto(prReviewList, isLast);
    }
}
