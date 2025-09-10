package com.moyoy.api.user.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moyoy.api.user.application.request.UserSyncData;
import com.moyoy.api.user.application.response.UserProfileQueryResult;
import com.moyoy.api.user.application.response.UserSyncResult;

import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingRepository;
import com.moyoy.domain.user.Role;
import com.moyoy.domain.user.SocialSize;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.domain.user.dto.UserCreate;
import com.moyoy.domain.user.error.UserGithubAccountTypeNotAllowException;
import com.moyoy.domain.user.error.UserNotFoundException;

import com.moyoy.infra.database.mysql.query.dto.UserRankingView;
import com.moyoy.infra.database.mysql.query.port.UserRankingReader;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRankingReader userRankingReader;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RankingRepository rankingRepository;

	@Nested
	@DisplayName("유저 프로필 조회 시")
	class GetUserProfile {

		@Test
		@DisplayName("정상적으로 조회된다")
		void success() {

			// given
			Long userId = 1L;
			UserRankingView mockView = new UserRankingView(userId, "moyoy", 1000, "A", "https://profile.img");

			when(userRankingReader.findByUserId(userId))
				.thenReturn(Optional.of(mockView));

			// when
			UserProfileQueryResult result = userService.getUserProfile(userId);

			// then
			assertThat(result.userId()).isEqualTo(userId);
			assertThat(result.username()).isEqualTo("moyoy");
			assertThat(result.yearlyRankPoint()).isEqualTo(1000);
			assertThat(result.yearlyRankGrade()).isEqualTo("A");
			assertThat(result.profileImgUrl()).isEqualTo("https://profile.img");
		}

		@Test
		@DisplayName("주어진_userId에_해당하는_유저_프로필이_없으면_UserNotFoundException_예외가_발생한다")
		void not_found_exception() {

			Long userId = 999L;
			when(userRankingReader.findByUserId(userId))
				.thenReturn(Optional.empty());

			assertThatThrownBy(() -> userService.getUserProfile(userId))
				.isInstanceOf(UserNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("syncOrSignUp 호출시 ")
	class SyncOrSignUp {

		@Test
		@DisplayName("기존 회원이면 프로필을 업데이트한다")
		void update_existing_user_profile() {

			// given
			UserSyncData data = new UserSyncData(1234, "newName", "newImg", "User", 100, 10);

			User existingUser = User.builder()
				.id(1L)
				.githubUserId(1234)
				.username("oldName")
				.profileImgUrl("oldImg")
				.socialSize(SocialSize.SMALL)
				.role(Role.USER)
				.build();

			when(userRepository.findByGithubUserId(1234))
				.thenReturn(Optional.of(existingUser));

			// when
			UserSyncResult result = userService.syncOrSignUp(data);

			// then
			assertThat(result.username()).isEqualTo("newName");
			assertThat(result.profileImgUrl()).isEqualTo("newImg");
			assertThat(result.socialSize()).isEqualTo(SocialSize.MEDIUM);
			verify(userRepository).save(any(User.class));
		}

		@Test
		@DisplayName("기존 회원이 아니면 회원가입을 한다")
		void sign_up_new_user() {

			// given
			UserSyncData data = new UserSyncData(1234, "newName", "newImg", "User", 100, 10);

			when(userRepository.findByGithubUserId(1234))
				.thenReturn(Optional.empty());

			SocialSize socialSize = SocialSize.of(data.followers(), data.following());
			UserCreate userCreate = UserCreate.of(data.githubUserId(), data.username(), data.profileImgUrl(), socialSize);
			User newUser = User.from(userCreate);

			User savedUser = User.builder()
				.id(1L)
				.githubUserId(newUser.getGithubUserId())
				.username(newUser.getUsername())
				.profileImgUrl(newUser.getProfileImgUrl())
				.socialSize(newUser.getSocialSize())
				.role(newUser.getRole())
				.build();

			when(userRepository.save(any(User.class)))
				.thenReturn(savedUser);

			// when
			UserSyncResult result = userService.syncOrSignUp(data);

			// then
			assertThat(result.id()).isEqualTo(1L);
			assertThat(result.githubUserId()).isEqualTo(1234);
			assertThat(result.username()).isEqualTo("newName");
			assertThat(result.profileImgUrl()).isEqualTo("newImg");
			assertThat(result.socialSize()).isEqualTo(SocialSize.MEDIUM);
			assertThat(result.role()).isEqualTo(Role.USER);

			verify(userRepository).save(any(User.class));
			verify(rankingRepository).save(any(Ranking.class));
		}

		@Test
		@DisplayName("기존회원이 아니지만 깃허브 Account Type이 User가 아니면 가입할 수 없다")
		void organization_account_cannot_sign_up() {

			// given
			UserSyncData data = new UserSyncData(1234, "newName", "newImg", "Organization", 100, 10);

			when(userRepository.findByGithubUserId(1234))
				.thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> userService.syncOrSignUp(data))
				.isInstanceOf(UserGithubAccountTypeNotAllowException.class);
		}
	}
}
