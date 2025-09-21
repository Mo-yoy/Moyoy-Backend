package com.moyoy.common.page;

import java.util.List;

public record PageResult<T>(
	List<T> content,
	int pageNumber,
	int pageSize,
	long totalElements,
	int totalPages,
	boolean isLast,
	boolean hasNext) {
}
