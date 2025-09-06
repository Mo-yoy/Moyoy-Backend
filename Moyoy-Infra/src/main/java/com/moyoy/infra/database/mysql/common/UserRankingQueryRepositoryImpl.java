package com.moyoy.infra.database.mysql.common;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRankingQueryRepositoryImpl implements UserRankingQueryRepository {

	private final UserRankingQueryDslRepository userRankingQueryDslRepository;

	@Override
	public Optional<UserRankingView> findByUserId(Long userId) {
		return userRankingQueryDslRepository.findUserRankingView(userId);
	}
}
