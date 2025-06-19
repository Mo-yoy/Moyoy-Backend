package com.moyo.backend.ranking.implement;

import com.moyo.backend.ranking.presentation.dto.RankingSearchRequest;
import com.moyo.backend.user.domain.User;
import com.moyo.backend.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingReader {

    private final UserRepository userRepository;

    public Slice<User> getAllUserRankings(RankingSearchRequest rankingSearch) {

        Sort sort = Sort.by(Sort.Direction.DESC, "ranking."+rankingSearch.getDuration().getValue());
        Pageable pageable = PageRequest.of(rankingSearch.getPage(), rankingSearch.getSize(), sort);

        return userRepository.findAll(pageable);
    }
}