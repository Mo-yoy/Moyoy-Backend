package com.moyo.backend.domain.github_ranking.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.data_access.RankingRepository;
import com.moyo.backend.domain.user.data_access.UserRepository;
import com.moyo.backend.domain.user.implement.User;

@Component
@RequiredArgsConstructor
public class RankingReader {

	private final UserRepository userRepository;
	private final RankingRepository rankingRepository;

	public Slice<User> getAllUserRankings(RankingSearch rankingSearch) {

		Sort sort = Sort.by(Sort.Direction.DESC, "ranking." + rankingSearch.duration().getAttributeName());
		Pageable pageable = PageRequest.of(rankingSearch.page(), rankingSearch.size(), sort);

		return userRepository.findAll(pageable);
	}

	public Ranking getRanking(Long userId) {

		return rankingRepository.findById(userId).orElseThrow();
	}
}
