package com.moyoy.infra.database.mysql.query;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.infra.database.mysql.query.dto.UserRankingView;
import com.moyoy.infra.database.mysql.query.port.UserRankingReader;

@Component
@RequiredArgsConstructor
class UserRankingReaderImpl implements UserRankingReader {

	private final UserRankingQueryDslRepository userRankingQueryDslRepository;

	@Override
	public Optional<UserRankingView> findByUserId(Long userId) {
		return userRankingQueryDslRepository.findUserRankingView(userId);
	}
}
