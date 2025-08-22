package com.moyoy.infra.database.pr_review;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.moyoy.domain.pr_review.*;
import com.moyoy.domain.support.error.user.UserNotFoundException;
import com.moyoy.domain.support.page.PageData;
import com.moyoy.domain.support.page.SliceResult;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import static com.moyoy.domain.pr_review.Status.OPEN;
import static com.moyoy.infra.database.pr_review.SortConverter.*;

@Repository
@RequiredArgsConstructor
public class PrReviewRepositoryImpl implements PrReviewRepository {

	private final PrReviewJpaRepository prReviewJpaRepository;
	private final UserRepository userRepository;

	@Override
	public PrReview save(PrReview prReview) {

		PrReviewEntity prReviewEntity = PrReviewMapper.toEntity(prReview);
		PrReviewEntity newPrReviewEntity = prReviewJpaRepository.save(prReviewEntity);
		return PrReviewMapper.toModel(newPrReviewEntity);
	}

	///  TODO : 보기 힘들지 않나?
	@Override
	public Optional<PrReview> findById(Long reviewId) {
		return prReviewJpaRepository.findById(reviewId).map(e -> {
			User user = userRepository.findById(e.getUserId()).orElseThrow(UserNotFoundException::new);

			return PrReviewMapper.toModel(e, PrReviewMapper.toAuthor(user));
		});
	}

	@Override
	public SliceResult<PrReview> findAllByStatusAndPosition(Status status, Position position, PageData pageData) {
		Sort sort = toSort(pageData.sort());
		Pageable pageable = PageRequest.of(pageData.page(), pageData.size(), sort);

		Slice<PrReviewEntity> slice = prReviewJpaRepository.findAllByStatusAndPosition(status, position, pageable);
		return mapSliceWithAuthors(slice);
	}

	@Override
	public SliceResult<PrReview> findAllByUserIdAndStatusAndPosition(Long userId, Status status, Position position, PageData pageData) {
		Sort sort = toSort(pageData.sort());
		Pageable pageable = PageRequest.of(pageData.page(), pageData.size(), sort);

		Slice<PrReviewEntity> slice = prReviewJpaRepository.findAllByUserIdAndStatusAndPosition(userId, status, position, pageable);

		Author author = userRepository.findById(userId).map(PrReviewMapper::toAuthor).orElseThrow(UserNotFoundException::new);

		List<PrReview> content = slice.getContent().stream()
				.map(e -> PrReviewMapper.toModel(e, author))
				.toList();

		return SliceResult.of(content, slice.hasNext());
	}

	@Override
	public Long update(PrReview review) {
		PrReviewEntity reviewEntity = PrReviewMapper.toEntity(review);
		prReviewJpaRepository.save(reviewEntity);

		return reviewEntity.getId();
	}

	@Override
	public void deleteById(Long reviewId) {
		prReviewJpaRepository.deleteById(reviewId);
	}

	///  TODO : 테스트가 필요할까?
	private SliceResult<PrReview> mapSliceWithAuthors(Slice<PrReviewEntity> slice) {
		List<Long> ids = slice.getContent().stream()
				.map(PrReviewEntity::getUserId)
				.distinct()
				.toList();

		Map<Long, User> userMap = userRepository.findByIdIn(ids).stream()
				.collect(Collectors.toMap(User::getId, Function.identity()));

		List<PrReview> content = slice.getContent().stream()
				.map(e -> PrReviewMapper.toModel(e, PrReviewMapper.toAuthor(userMap.get(e.getUserId()))))
				.toList();

		return SliceResult.of(content, slice.hasNext());
	}
}
