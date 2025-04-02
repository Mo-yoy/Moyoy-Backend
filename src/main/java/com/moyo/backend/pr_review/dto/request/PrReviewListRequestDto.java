package com.moyo.backend.pr_review.dto.request;

import com.moyo.backend.pr_review.domain.position.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
public class PrReviewListRequestDto {

    private String status;
    private String order;
    private String position;
    private int page;
    private int size;

    public Boolean getStatus() {
        return "open".equalsIgnoreCase(status); // "open"이면 true, 아니면 false.
    }

    public Position getPositionEnum() {
        return position != null ? Position.fromString(position) : null;
    }

    public Sort toSort() {
        String[] tokens = order.split(",");
        if (tokens.length != 2) throw new IllegalArgumentException("맞지 않는 형식");

        Sort.Direction direction = Sort.Direction.fromString(tokens[1].toUpperCase());
        return Sort.by(direction, tokens[0]); // 예시: Sort.by(Sort.Direction.DESC, "createdAt").
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, toSort());
    }
}
