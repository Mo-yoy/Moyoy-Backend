package com.moyoy.domain.support.page;

// 정렬 필요해 지면 추후 추가
public record PageData(
	int page,
	int size) {
	public static PageData of(int page, int size) {
		return new PageData(page, size);
	}
}
