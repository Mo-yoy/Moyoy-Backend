package com.moyo.backend.domain.pr_review.implement;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.moyo.backend.common.exception.CommonErrorCode;
import com.moyo.backend.common.exception.MoyoException;
import com.moyo.backend.domain.pr_review.data_access.PrReviewRepository;
import com.moyo.backend.domain.pr_review.implement.dto.PrReviewContentData;
import com.moyo.backend.domain.pr_review.implement.dto.PrReviewDetail;
import com.moyo.backend.domain.pr_review.implement.dto.PrReviewListData;
import com.moyo.backend.domain.pr_review.implement.dto.PrReviewSummary;

@Component
@RequiredArgsConstructor
public class PrReviewReader {

	private final PrReviewRepository prReviewRepository;

	public PrReviewListData readListByCriteria(String status, String order, String position, int page, int size) {

		Sort sort = sortByOrder(order);

		Pageable pageable = PageRequest.of(page, size, sort);

		Slice<PrReview> slice = prReviewRepository.findAllByStatusAndPosition(
			"open".equalsIgnoreCase(status),
			Position.from(position),
			pageable);

		List<PrReviewSummary> prReviews = slice.getContent().stream()
			.map(PrReviewSummary::from)
			.toList();

		return new PrReviewListData(prReviews, slice.hasNext());
	}

	public PrReviewListData readMyListByCriteria(Long userId, String status, String order, String position, int page, int size) {

		Sort sort = sortByOrder(order);

		Pageable pageable = PageRequest.of(page, size, sort);

		Slice<PrReview> slice = prReviewRepository.findAllByUserIdAndStatusAndPosition(
			userId,
			"open".equalsIgnoreCase(status),
			Position.from(position),
			pageable);

		List<PrReviewSummary> prReviews = slice.getContent().stream()
			.map(PrReviewSummary::from)
			.toList();

		return new PrReviewListData(prReviews, slice.hasNext());
	}

	public PrReviewDetail readPrReviewDetail(Long reviewId, Long userId) {

		// 유효하지 않은 reviewId 검증.
		PrReview prReview = prReviewRepository.findById(reviewId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.INVALID_PARAM));

		return PrReviewDetail.from(prReview, userId);
	}

	public PrReviewContentData readPrReviewContent(Long reviewId, Long userId) {

		PrReview prReview = prReviewRepository.findById(reviewId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.INVALID_PARAM));

		return PrReviewContentData.from(prReview, userId);
	}

	public Sort sortByOrder(String order) {
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
}
