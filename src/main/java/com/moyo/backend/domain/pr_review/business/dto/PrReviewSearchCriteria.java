package com.moyo.backend.domain.pr_review.business.dto;

import com.moyo.backend.domain.pr_review.implement.dto.SearchCriteria;

public record PrReviewSearchCriteria(
	String status,
	String order,
	String position,
	int page,
	int size) {
	public SearchCriteria toCriteria() {
		return new SearchCriteria(status, order, position, page, size);
	}
}
