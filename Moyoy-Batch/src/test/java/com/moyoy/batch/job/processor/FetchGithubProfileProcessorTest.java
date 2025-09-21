package com.moyoy.batch.job.processor;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moyoy.batch.job.processor.dto.UserAuthContext;
import com.moyoy.batch.job.processor.dto.UserProfileContext;

import com.moyoy.infra.external.github.user.GithubUserClient;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

@ExtendWith(MockitoExtension.class)
class FetchGithubProfileProcessorTest {

	@Mock
	private GithubUserClient githubUserClient;

	@InjectMocks
	private FetchGithubProfileProcessor processor;

	@Test
	void 유효한_UserAuthContext가_들어오면_UserProfileContext를_반환한다() throws Exception {

		// given
		UserAuthContext auth = new UserAuthContext(1L, 123, "mock-token");

		GithubUserResponse mockResponse = GithubUserResponse.createForTest("moyoy", 123, "img", 5, 5);
		given(githubUserClient.fetchUser("mock-token", 123))
			.willReturn(mockResponse);

		// when
		UserProfileContext result = processor.process(auth);

		// then
		Assertions.assertNotNull(result);
		assertThat(result.auth()).isEqualTo(auth);
		assertThat(result.username()).isEqualTo(mockResponse.login());
		assertThat(result.followerCount()).isEqualTo(mockResponse.followers());
	}
}
