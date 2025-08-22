package com.moyoy.domain.support.page;

public record PageData(
	int page,
	int size,
	String sort
	) {
	public static PageData of(int page, int size) {
		return new PageData(page, size, null);
	}

	public static PageData of(int page, int size, String sort) {
		return new PageData(page, size, sort);
	}
}
