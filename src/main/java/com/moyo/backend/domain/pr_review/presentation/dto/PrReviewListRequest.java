package com.moyo.backend.domain.pr_review.presentation.dto;

import com.moyo.backend.domain.pr_review.business.dto.PrReviewSearchCriteria;

import jakarta.validation.constraints.NotBlank;

public record PrReviewListRequest(
	@NotBlank String status,
	@NotBlank String order,
	String position,
	Integer page,
	Integer size) {
	public PrReviewListRequest {
		status = (status == null) ? "open" : status;
		order = (order == null) ? "createdAt,desc" : order;
		page = (page == null) ? 0 : page;
		size = (size == null) ? 10 : size;
	}

	public PrReviewSearchCriteria toCriteria() {
		return new PrReviewSearchCriteria(status, order, position, page, size);
	}
}
