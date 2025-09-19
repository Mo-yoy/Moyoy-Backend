package com.moyoy.batch.job.processor;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moyoy.batch.job.processor.dto.UserAuthContext;

import com.moyoy.domain.user.error.UserGithubTokenNotFoundException;

import com.moyoy.infra.database.mysql.query.port.GithubTokenReader;
import com.moyoy.infra.database.mysql.user.UserEntity;

@ExtendWith(MockitoExtension.class)
class CreateUserAuthContextProcessorTest {

	@Mock
	private GithubTokenReader githubTokenReader;

	@InjectMocks
	private CreateUserAuthContextProcessor processor;

	@Test
	void 유효한_토큰이_있으면_process_호출시_UserAuthContext_반환한다() throws Exception {

		// given
		UserEntity userEntity = UserEntity.builder()
			.id(1L)
			.githubUserId(123)
			.build();

		String expectedToken = "mock-token";

		given(githubTokenReader.findAccessBearerToken(1L))
			.willReturn(Optional.of(expectedToken));

		// when
		UserAuthContext result = processor.process(userEntity);

		// then
		assertNotNull(result);
		assertThat(result.userId()).isEqualTo(1L);
		assertThat(result.githubUserId()).isEqualTo(123);
		assertThat(result.githubAccessToken()).isEqualTo(expectedToken);
	}

	@Test
	void 토큰이_없으면_process_호출시_UserGithubTokenNotFoundException_예외를_던진다() {

		// given
		UserEntity userEntity = UserEntity.builder()
			.id(1L)
			.githubUserId(123)
			.build();

		given(githubTokenReader.findAccessBearerToken(1L))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> processor.process(userEntity))
			.isInstanceOf(UserGithubTokenNotFoundException.class);
	}
}
