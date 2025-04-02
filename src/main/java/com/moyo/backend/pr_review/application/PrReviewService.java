package com.moyo.backend.pr_review.application;

import com.moyo.backend.common.exception.CommonErrorCode;
import com.moyo.backend.common.exception.MoyoException;
import com.moyo.backend.pr_review.application.port.PrReviewHitsRepository;
import com.moyo.backend.pr_review.application.port.PrReviewRepository;
import com.moyo.backend.pr_review.domain.PrReview;
import com.moyo.backend.pr_review.domain.PrReviewHits;
import com.moyo.backend.pr_review.dto.PrReviewDto;
import com.moyo.backend.pr_review.dto.request.PrReviewCreateRequestDto;
import com.moyo.backend.pr_review.dto.request.PrReviewListRequestDto;
import com.moyo.backend.pr_review.dto.request.PrReviewUpdateRequestDto;
import com.moyo.backend.pr_review.dto.response.*;
import com.moyo.backend.user.User;
import com.moyo.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PrReviewService {

    private final PrReviewRepository prReviewRepository;
    private final UserRepository userRepository;
    private final PrReviewHitsRepository prReviewHitsRepository;

    public PrReviewListResponseDto getPrReviewList(PrReviewListRequestDto requestDto) {

        Page<PrReview> prReviews = prReviewRepository.findAllByStatusAndPosition(
                requestDto.getStatus(),
                requestDto.getPosition(),
                requestDto.toPageable()
        );

        List<PrReviewDto> prReviewDtoList = prReviews.getContent()
                .stream()
                .map(pr -> new PrReviewDto(
                        pr.getUser().getProfileImgUrl(),
                        pr.getUser().getUsername(),
                        pr.getPosition().toString(),
                        pr.getTitle(),
                        pr.getHitCount(),
                        pr.getCreatedAt()
                ))
                .toList();

        return PrReviewListResponseDto.of(prReviewDtoList, prReviews.isLast());
    }

    public PrReviewDetailResponseDto getPrReviewDetail(Long reviewId, Long userId) {
        PrReview prReview = prReviewRepository.findById(reviewId)
                .orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

        boolean isWriter = false;

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

            isWriter = prReview.getUser().equals(user);

            // 작성자 본인은 조회수 증가에 포함되지 않음.
            if (!isWriter) {
                boolean hasViewed = prReviewHitsRepository.existsByPrReviewIdAndUserId(reviewId, user.getId());

                if (!hasViewed) {
                    prReview.increaseHitCount();

                    PrReviewHits prReviewHits = new PrReviewHits(user, prReview);

                    prReviewHitsRepository.save(prReviewHits);
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
                prReview.getPrUrl()
        );
    }

    public PrReviewCreateResponseDto createPrReview(PrReviewCreateRequestDto requestDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

        PrReview review = PrReview.builder()
                .title(requestDto.getTitle())
                .user(user)
                .position(requestDto.getPositionEnum())
                .prUrl(requestDto.getPrUrl())
                .content(requestDto.getContent())
                .hitCount(0)
                .status(true) // 현재 opened 인지는 github에 상태 확인해봐야 함.
                .build();

        PrReview savedReview = prReviewRepository.save(review);

        return new PrReviewCreateResponseDto(savedReview.getId());
    }

    public PrReviewUpdateFormResponseDto getUpdateForm(Long reviewId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

        PrReview review = prReviewRepository.findById(reviewId)
                .orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new MoyoException(CommonErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
        }

        return new PrReviewUpdateFormResponseDto(review.getTitle(), review.getPosition().toString(), review.getPrUrl(), review.getContent());
    }

    public PrReviewUpdateResponseDto updatePrReview(PrReviewUpdateRequestDto requestDto, Long reviewId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

        PrReview review = prReviewRepository.findById(reviewId)
                .orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new MoyoException(CommonErrorCode.PR_REVIEW_EDIT_FORBIDDEN);
        }

        review.updateDetail(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getPrUrl(),
                requestDto.getPositionEnum()
        );

        return new PrReviewUpdateResponseDto(reviewId);
    }

    public void deletePrReview(Long reviewId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

        PrReview review = prReviewRepository.findById(reviewId)
                .orElseThrow(() -> new MoyoException(CommonErrorCode.PR_REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new MoyoException(CommonErrorCode.PR_REVIEW_DELETE_FORBIDDEN);
        }

        prReviewHitsRepository.deleteByPrReview(review);

        prReviewRepository.delete(review);
    }

    public PrReviewListResponseDto getMyPrReviewList(PrReviewListRequestDto requestDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MoyoException(CommonErrorCode.USER_NOT_FOUND));

        Page<PrReview> prReviews = prReviewRepository.findAllByUserAndStatusAndPosition(
                user,
                requestDto.getStatus(),
                requestDto.getPosition(),
                requestDto.toPageable()
        );

        List<PrReviewDto> prReviewDtoList = prReviews.getContent()
                .stream()
                .map(pr -> new PrReviewDto(
                        pr.getUser().getProfileImgUrl(),
                        pr.getUser().getUsername(),
                        pr.getPosition().toString(),
                        pr.getTitle(),
                        pr.getHitCount(),
                        pr.getCreatedAt()
                ))
                .toList();

        return PrReviewListResponseDto.of(prReviewDtoList, prReviews.isLast());
    }
}
