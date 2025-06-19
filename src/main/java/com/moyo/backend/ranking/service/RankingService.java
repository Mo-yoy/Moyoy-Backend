package com.moyo.backend.ranking.service;

import com.moyo.backend.ranking.implement.RankingReader;
import com.moyo.backend.ranking.presentation.dto.RankingSearchRequest;
import com.moyo.backend.ranking.presentation.dto.RankingSearchResponse;
import com.moyo.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingReader rankingReader;

    public RankingSearchResponse getAllUserRanking(RankingSearchRequest request) {

        Slice<User> allUserRankings = rankingReader.getAllUserRankings(request);

        return RankingSearchResponse.from(allUserRankings, request.getDuration());
    }
}