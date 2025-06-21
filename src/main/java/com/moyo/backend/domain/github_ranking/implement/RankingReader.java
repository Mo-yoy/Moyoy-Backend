package com.moyo.backend.domain.github_ranking.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.presentation.dto.RankingSearchRequest;
import com.moyo.backend.domain.user.domain.User;
import com.moyo.backend.domain.user.domain.UserRepository;

@Component
@RequiredArgsConstructor
public class RankingReader {

	private final UserRepository userRepository;

	public Slice<User> getAllUserRankings(RankingSearchRequest rankingSearch) {

		Sort sort = Sort.by(Sort.Direction.DESC, "ranking." + rankingSearch.getDuration().getValue());
		Pageable pageable = PageRequest.of(rankingSearch.getPage(), rankingSearch.getSize(), sort);

		return userRepository.findAll(pageable);
	}
}
