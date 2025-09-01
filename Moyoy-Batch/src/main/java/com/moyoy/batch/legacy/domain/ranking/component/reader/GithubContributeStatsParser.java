package com.moyoy.batch.legacy.domain.ranking.component.reader;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.moyoy.infra.external.github.repo.GithubRepoCommitStatsResponse;

@Component
@RequiredArgsConstructor
public class GithubContributeStatsParser {

	private final ObjectMapper objectMapper;

	public List<GithubRepoCommitStatsResponse> parseGithubContributeStats(ResponseEntity<?> response) {

		if (response.getStatusCode().value() != 200) {
			throw new RuntimeException("repo 통계 데이터를 불러오지 못했습니다.");
		}

		List<GithubRepoCommitStatsResponse> repoContributorStatResponses;

		try {
			repoContributorStatResponses = objectMapper.readValue((String)response.getBody(), new TypeReference<>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON 파싱 중 에러 발생", e);
		}

		return repoContributorStatResponses;
	}
}
