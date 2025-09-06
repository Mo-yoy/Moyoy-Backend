package com.moyoy.api.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.moyoy.api.user.application.response.UserProfileQueryResult;

import com.moyoy.domain.support.error.user.UserNotFoundException;

import com.moyoy.infra.database.mysql.common.UserRankingQueryRepository;
import com.moyoy.infra.database.mysql.common.UserRankingView;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

	private final UserRankingQueryRepository userRankingQueryRepository;

	///  도메인 모델이 필요없는 단순 조회
	public UserProfileQueryResult getUserProfile(Long userId) {

		UserRankingView userRankingView = userRankingQueryRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);

		return UserProfileQueryResult.from(userRankingView);
	}
}
