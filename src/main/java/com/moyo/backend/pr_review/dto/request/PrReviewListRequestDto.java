package com.moyo.backend.pr_review.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.moyo.backend.common.exception.CommonErrorCode;
import com.moyo.backend.common.exception.MoyoException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrReviewListRequestDto {

	private String status;
	private String order;
	private String position;
	private int page;
	private int size;

	public Boolean getStatus() {
		return "open".equalsIgnoreCase(status);
	}

	public Sort toSort() {
		String[] tokens = order.split(",");
		if (tokens.length != 2) {
			throw new MoyoException(CommonErrorCode.INVALID_PARAM);
		}

		try {
			Sort.Direction direction = Sort.Direction.fromString(tokens[1].toUpperCase());
			return Sort.by(direction, tokens[0]); // 예시: Sort.by(Sort.Direction.DESC, "createdAt").
		} catch (IllegalArgumentException e) {
			throw new MoyoException(CommonErrorCode.PARAM_TYPE_MISMATCH);
		}
	}

	public Pageable toPageable() {
		return PageRequest.of(page, size, toSort());
	}
}
