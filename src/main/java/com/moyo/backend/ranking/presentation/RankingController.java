package com.moyo.backend.ranking.presentation;

import com.moyo.backend.common.dto.ApiResponse;
import com.moyo.backend.ranking.presentation.dto.RankingSearchRequest;
import com.moyo.backend.ranking.presentation.dto.RankingSearchResponse;
import com.moyo.backend.ranking.service.RankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/rankings")
    public ResponseEntity<ApiResponse<RankingSearchResponse>> getAllUserRanking (
            @Valid RankingSearchRequest request
    ){

        RankingSearchResponse allUserRanking = rankingService.getAllUserRanking(request);

        return ResponseEntity.ok(ApiResponse.success(allUserRanking));
    }
}
