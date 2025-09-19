package com.moyoy.batch.job.processor;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.moyoy.batch.job.processor.dto.UserAuthContext;

import com.moyoy.domain.user.error.UserGithubTokenNotFoundException;

import com.moyoy.infra.database.mysql.query.port.GithubTokenReader;
import com.moyoy.infra.database.mysql.user.UserEntity;

@Component
@RequiredArgsConstructor
public class CreateUserAuthContextProcessor implements ItemProcessor<UserEntity, UserAuthContext> {

	private final GithubTokenReader githubTokenReader;

	@Override
	public UserAuthContext process(UserEntity userEntity) {

		String githubAccessToken = githubTokenReader.findAccessBearerToken(userEntity.getId())
			.orElseThrow(UserGithubTokenNotFoundException::new);

		return new UserAuthContext(userEntity.getId(), userEntity.getGithubUserId(), githubAccessToken);
	}
}
