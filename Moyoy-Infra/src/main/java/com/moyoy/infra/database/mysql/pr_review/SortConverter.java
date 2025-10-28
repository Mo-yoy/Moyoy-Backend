package com.moyoy.infra.database.mysql.pr_review;

import java.util.Arrays;

import org.springframework.data.domain.Sort;

import com.moyoy.domain.pr_review.SortableFields;

import com.moyoy.common.error.CommonErrorCode;
import com.moyoy.common.error.MoyoException;

/// Page 번호로 조회할 때 사용하던 것
public class SortConverter {
	public static Sort toSort(String sortParam) {
		if (sortParam == null || sortParam.isBlank()) {
			return Sort.unsorted();
		}

		try {
			return Sort.by(
				Arrays.stream(sortParam.split(","))
					.map(token -> {
						String[] parts = token.split("-");
						if (parts.length != 2) {
							throw new MoyoException(CommonErrorCode.INVALID_PARAM); // FIXME
						}

						String field = parts[0];
						Sort.Direction direction = Sort.Direction.fromString(parts[1]);

						if (!SortableFields.ALLOWED.contains(field)) {
							throw new MoyoException(CommonErrorCode.INVALID_PARAM); // FIXME
						}

						return new Sort.Order(direction, field);
					})
					.toList());
		} catch (IllegalArgumentException e) {
			throw new MoyoException(CommonErrorCode.INVALID_PARAM); // FIXME
		}
	}
}
