package com.moyoy.api.user.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moyoy.api.user.application.response.UserProfileQueryResult;

import com.moyoy.domain.support.error.user.UserNotFoundException;

import com.moyoy.infra.database.mysql.common.UserRankingQueryRepository;
import com.moyoy.infra.database.mysql.common.UserRankingView;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

	@InjectMocks
	private UserProfileService userProfileService;

	@Mock
	private UserRankingQueryRepository userRankingQueryRepository;

	@Test
	void 유저_프로필을_정상적으로_조회한다() {

		// given
		Long userId = 1L;

		UserRankingView mockView = new UserRankingView(userId, "moyoy", 1000, "A", "https://profile.img");

		when(userRankingQueryRepository.findByUserId(userId))
			.thenReturn(Optional.of(mockView));

		// when
		UserProfileQueryResult result = userProfileService.getUserProfile(userId);

		// then
		assertThat(result.userId()).isEqualTo(userId);
		assertThat(result.username()).isEqualTo("moyoy");
		assertThat(result.yearlyRankPoint()).isEqualTo(1000);
		assertThat(result.yearlyRankGrade()).isEqualTo("A");
		assertThat(result.profileImgUrl()).isEqualTo("https://profile.img");
	}

	@Test
	void 주어진_userId에_해당하는_유저_프로필이_없으면_UserNotFoundException_예외가_발생한다() {

		// given
		Long userId = 999L;
		when(userRankingQueryRepository.findByUserId(userId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> userProfileService.getUserProfile(userId))
			.isInstanceOf(UserNotFoundException.class);
	}
}
