package com.moyo.backend.domain.pr_review.business;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.moyo.backend.common.exception.CommonErrorCode;
import com.moyo.backend.common.exception.MoyoException;
import com.moyo.backend.domain.pr_review.data_access.PrReviewHitsRepository;
import com.moyo.backend.domain.pr_review.data_access.PrReviewRepository;
import com.moyo.backend.domain.pr_review.dto.request.PrReviewCreateRequestDto;
import com.moyo.backend.domain.pr_review.dto.request.PrReviewUpdateRequestDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewCreateResponseDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewUpdateFormResponseDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewUpdateResponseDto;
import com.moyo.backend.domain.pr_review.implement.Position;
import com.moyo.backend.domain.pr_review.implement.PrReview;
import com.moyo.backend.domain.pr_review.implement.PrReviewHits;
import com.moyo.backend.domain.pr_review.presentation.dto.PrReviewListResponse;
import com.moyo.backend.domain.user.data_access.UserRepository;
import com.moyo.backend.domain.user.implement.User;

@Slf4j
//@Service
@Transactional
@RequiredArgsConstructor
public class PrReviewServicelegacy {

	private final PrReviewRepository prReviewRepository;
	private final UserRepository userRepository;
	private final PrReviewHitsRepository prReviewHitsRepository;

	private void validateUserExists(Long userId) {

		if (userId == null || !userRepository.existsById(userId)) {
			throw new MoyoException(CommonErrorCode.USER_NOT_FOUND);
		}
	}

	public PrReviewListResponse getPrReviewList(PrReviewListRequestDto requestDto) {

		Page<PrReview> prReviews = prReviewRepository.findAllByStatusAndPosition(
			requestDto.getStatus(),
			Position.from(requestDto.getPosition()),
			requestDto.toPageable());

		List<PrReviewDto> prReviewDtoList = prReviews.getContent()
			.stream()
			.map(pr -> new PrReviewDto(
				pr.getUser().getProfileImgUrl(),
				pr.getUser().getUsername(),
				pr.getPosition().toString(),
				pr.getTitle(),
				pr.getHitCount(),
				pr.getCreatedAt()))
			.toList();

		return PrReviewListResponse.of(prReviewDtoList, prReviews.isLast());
	}

	public PrReviewDetailResponseDto getPrReviewDetail(Long reviewId, Long userId) {

		PrReview prReview = prReviewRepository.findById(reviewId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

		boolean isWriter = false;

		// 비로그인 유저도 있기 때문.
		if (userId != null) {
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

			isWriter = prReview.getUser().equals(user);

			// 작성자 본인은 조회수 증가에 포함되지 않음.
			if (!isWriter) {
				boolean hasViewed = prReviewHitsRepository.existsByPrReviewIdAndUserId(reviewId, user.getId());

				if (!hasViewed) {
					prReview.increaseHitCount();

					prReviewHitsRepository.save(new PrReviewHits(user, prReview));
				}
			}
		}

		return new PrReviewDetailResponseDto(
			prReview.getStatus() ? "open" : "closed",
			isWriter,
			prReview.getUser().getProfileImgUrl(),
			prReview.getUser().getUsername(),
			prReview.getPosition().toString(),
			prReview.getTitle(),
			prReview.getHitCount(),
			prReview.getCreatedAt().toString(),
			prReview.getContent(),
			prReview.getPrUrl());
	}

	public PrReviewCreateResponseDto createPrReview(PrReviewCreateRequestDto requestDto, Long userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

		PrReview review = PrReview.builder()
			.title(requestDto.getTitle())
			.user(user)
			.position(Position.from(requestDto.getPosition()))
			.prUrl(requestDto.getPrUrl())
			.content(requestDto.getContent())
			.hitCount(0)
			.status(true) // 현재 상태를 깃허브에서 확인하고 가져와야 함. FIXME
			.build();

		PrReview savedReview = prReviewRepository.save(review);

		return new PrReviewCreateResponseDto(savedReview.getId());
	}

	public PrReviewUpdateFormResponseDto getUpdateForm(Long reviewId, Long userId) {

		validateUserExists(userId);

		PrReview review = prReviewRepository.findById(reviewId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

		if (!review.getUser().getId().equals(userId)) {
			throw new MoyoException(CommonErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
		}

		return new PrReviewUpdateFormResponseDto(review.getTitle(), review.getPosition().toString(), review.getPrUrl(), review.getContent());
	}

	public PrReviewUpdateResponseDto updatePrReview(PrReviewUpdateRequestDto requestDto, Long reviewId, Long userId) {

		validateUserExists(userId);

		PrReview review = prReviewRepository.findById(reviewId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

		if (!review.getUser().getId().equals(userId)) {
			throw new MoyoException(CommonErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
		}

		review.updateDetail(
			requestDto.getTitle(),
			requestDto.getContent(),
			requestDto.getPrUrl(),
			Position.from(requestDto.getPosition()));

		return new PrReviewUpdateResponseDto(reviewId);
	}

	public void deletePrReview(Long reviewId, Long userId) {

		validateUserExists(userId);

		PrReview review = prReviewRepository.findById(reviewId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

		if (!review.getUser().getId().equals(userId)) {
			throw new MoyoException(CommonErrorCode.PR_REVIEW_DELETE_FORBIDDEN);
		}

		prReviewHitsRepository.deleteByPrReview(review);

		prReviewRepository.delete(review);
	}

	public PrReviewListResponse getMyPrReviewList(PrReviewListRequestDto requestDto, Long userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

		Page<PrReview> prReviews = prReviewRepository.findAllByUserAndStatusAndPosition(
			user,
			requestDto.getStatus(),
			Position.from(requestDto.getPosition()),
			requestDto.toPageable());

		List<PrReviewDto> prReviewDtoList = prReviews.getContent()
			.stream()
			.map(pr -> new PrReviewDto(
				pr.getUser().getProfileImgUrl(),
				pr.getUser().getUsername(),
				pr.getPosition().toString(),
				pr.getTitle(),
				pr.getHitCount(),
				pr.getCreatedAt()))
			.toList();

		return PrReviewListResponse.of(prReviewDtoList, prReviews.isLast());
	}
}
