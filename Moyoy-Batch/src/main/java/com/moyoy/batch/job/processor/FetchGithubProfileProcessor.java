package com.moyoy.batch.job.processor;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.moyoy.batch.job.processor.dto.UserAuthContext;
import com.moyoy.batch.job.processor.dto.UserProfileContext;

import com.moyoy.infra.external.github.user.GithubUserClient;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

@Component
@RequiredArgsConstructor
public class FetchGithubProfileProcessor implements ItemProcessor<UserAuthContext, UserProfileContext> {

	private final GithubUserClient githubUserClient;

	@Override
	public UserProfileContext process(UserAuthContext auth) {

		GithubUserResponse githubUserResponse = githubUserClient.fetchUser(auth.githubAccessToken(), auth.githubUserId());

		return new UserProfileContext(auth, githubUserResponse.login(), githubUserResponse.followers());
	}
}
