package com.moyo.backend.domain.github_ranking.temporary_batch;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyo.backend.common.annotation.LoginUserId;

@RestController
@RequiredArgsConstructor
public class RankingTestBatchController {

	private final RankingTestBatchService rankingTestBatchService;

	@GetMapping("/test/ranking/batch")
	public void batchTest(@LoginUserId Long currentUserId) {

		rankingTestBatchService.batchExec(currentUserId);
	}
}
