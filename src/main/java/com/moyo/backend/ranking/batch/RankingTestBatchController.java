package com.moyo.backend.ranking.batch;

import com.moyo.backend.security.annotation.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankingTestBatchController {

    private final RankingTestBatchService rankingTestBatchService;

    @GetMapping("/test/ranking/batch")
    public void batchTest(@CurrentUserId Long currentUserId){

        rankingTestBatchService.batchExec(currentUserId);
    }
}