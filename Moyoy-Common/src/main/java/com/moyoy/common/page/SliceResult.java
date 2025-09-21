package com.moyoy.common.page;

import java.util.List;

public record SliceResult<T>(
	List<T> content,
	boolean isLast,
	boolean hasNext) {

	public static <S> SliceResult<S> of(List<S> content, boolean hasNext) {
		return new SliceResult<>(content, !hasNext, hasNext);
	}
}
