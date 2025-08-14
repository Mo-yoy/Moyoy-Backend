package com.moyoy.core.domain.pr_review.implement.dto;

public record SearchCriteria(
	String status,
	String order,
	String position,
	int page,
	int size) {
}
