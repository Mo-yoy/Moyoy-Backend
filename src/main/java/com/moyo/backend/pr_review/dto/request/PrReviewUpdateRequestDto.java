package com.moyo.backend.pr_review.dto.request;

import com.moyo.backend.pr_review.domain.position.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrReviewUpdateRequestDto {

    private String title;
    private String position;
    private String prUrl;
    private String content;

    public Position getPositionEnum() {
        return position != null ? Position.fromString(position) : null;
    }
}
