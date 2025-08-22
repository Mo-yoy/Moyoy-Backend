package com.moyoy.api.pr_review.application;

import com.moyoy.api.pr_review.application.request.PrReviewContentData;
import com.moyoy.api.pr_review.application.response.PrReviewCreateResult;
import com.moyoy.api.pr_review.application.response.PrReviewUpdateResult;
import com.moyoy.api.pr_review.application.request.SearchCondition;
import com.moyoy.api.pr_review.application.response.PrReviewContentResult;
import com.moyoy.api.pr_review.application.response.PrReviewDetailResult;
import com.moyoy.api.pr_review.application.response.PrReviewListResult;
import com.moyoy.domain.pr_review.PrReview;
import com.moyoy.domain.pr_review.PrReviewCreate;
import com.moyoy.domain.pr_review.PrReviewRepository;
import com.moyoy.domain.support.error.pr_review.PrReviewDeleteForbiddenException;
import com.moyoy.domain.support.error.pr_review.PrReviewEditForbiddenException;
import com.moyoy.domain.support.error.pr_review.PrReviewNotFoundException;
import com.moyoy.domain.support.page.PageData;
import com.moyoy.domain.support.page.SliceResult;
import com.moyoy.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrReviewService {

	private final PrReviewRepository prReviewRepository;
	private final UserRepository userRepository;

	public PrReviewListResult getPrReviewList(SearchCondition condition) {

		SliceResult<PrReview> prReviewSlice = prReviewRepository.findAllByStatusAndPosition(
				condition.status(),
				condition.position(),
				PageData.of(condition.page(), condition.size(), condition.order())
		);

		return PrReviewListResult.from(prReviewSlice);
	}

	///  TODO : 범용적으로 getPrReviewList By UserId?
	public PrReviewListResult getMyPrReviewList(Long userId, SearchCondition condition) {

		SliceResult<PrReview> prReviewSlice = prReviewRepository.findAllByUserIdAndStatusAndPosition(
				userId,
				condition.status(),
				condition.position(),
				PageData.of(condition.page(), condition.size(), condition.order())
		);

		return PrReviewListResult.from(prReviewSlice);
	}

	public PrReviewDetailResult getPrReviewDetail(Long reviewId, Long userId) {

		PrReview prReview = prReviewRepository.findById(reviewId)
				.orElseThrow(PrReviewNotFoundException::new);

		// 2. 조회수 증가 관리 (중복 증가 방지 비교 분석 후 적용)
		// TODO

		return PrReviewDetailResult.from(prReview, prReview.getAuthor().id().equals(userId));
	}

	///  TODO : 보통은 Create, Update 스펙이 같을 수가 없어서 같이 안씀
	public PrReviewCreateResult createPrReview(PrReviewContentData content, Long userId) { // FIXME

		///  TODO : 나중에 수정, 임시 로직
		PrReviewCreate prReviewCreate = new PrReviewCreate(
				userId,
				content.title(),
				content.position(),
				content.prUrl(),
				content.content()
		);
		PrReview newPrReview = PrReview.create(prReviewCreate);

		Long createdReviewId = prReviewRepository.save(newPrReview).getId();

//		Long createdReviewId = prReviewRepository.create(prReviewCreate, userId);

		///  TODO : of vs create
		return PrReviewCreateResult.from(createdReviewId);
	}

	public PrReviewContentResult getPrReviewUpdateForm(Long reviewId, Long userId) {

		PrReview prReview = prReviewRepository.findById(reviewId)
				.orElseThrow(PrReviewNotFoundException::new);

		if (prReview.getAuthor().id().equals(userId)) {
			throw new PrReviewEditForbiddenException();
		}

		return PrReviewContentResult.from(prReview);
	}

	@Transactional
	public PrReviewUpdateResult updatePrReview(Long reviewId, PrReviewContentData content, Long userId) {

		PrReview prReview = prReviewRepository.findById(reviewId)
				.orElseThrow(PrReviewNotFoundException::new);

		if (!prReview.getAuthor().id().equals(userId)) {
			throw new PrReviewEditForbiddenException();
		}

		PrReviewCreate createContent = content.toCreateContent();

		prReview.updateDetail(createContent);

		prReviewRepository.update(prReview);

		return new PrReviewUpdateResult(prReview.getId());
	}

	@Transactional
	public void deletePrReview(Long reviewId, Long userId) {

		PrReview prReview = prReviewRepository.findById(reviewId)
				.orElseThrow(PrReviewNotFoundException::new);

		if (!prReview.getAuthor().id().equals(userId)) {
			throw new PrReviewDeleteForbiddenException();
		}

		if (prReview.getStatus().isclosed()) {
			throw new PrReviewDeleteForbiddenException();
		}

		prReviewRepository.deleteById(reviewId);
	}
}
