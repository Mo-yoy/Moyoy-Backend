package com.moyo.backend.pr_review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrReviewCreateRequestDto {

    private String title;
    private String position;
    private String prUrl;
    private String content;
}
