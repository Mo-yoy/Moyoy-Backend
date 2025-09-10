package com.moyoy.infra.external.github.support;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.Util;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubResponseParser {

	private final ObjectMapper objectMapper;

	public int extractRateLimit(Response response) {

		String rateLimitHeader = response.headers()
			.getOrDefault(GITHUB_RATE_LIMIT_HEADER, List.of())
			.stream()
			.findFirst()
			.orElse("0");

		return Integer.parseInt(rateLimitHeader);
	}

	public <T> T parseBody(Response response, Class<T> clazz) {

		try (Reader reader = response.body().asReader(StandardCharsets.UTF_8)) {

			String body = Util.toString(reader);
			return objectMapper.readValue(body, clazz);
		} catch (IOException e) {

			log.error("GitHub API response 파싱 실패", e);
			throw new IllegalStateException("Failed to parse GitHub API response", e);
		}
	}

	public <T> T parseBody(Response response, TypeReference<T> typeRef) {
		try (Reader reader = response.body().asReader(StandardCharsets.UTF_8)) {

			String body = Util.toString(reader);
			return objectMapper.readValue(body, typeRef);
		} catch (IOException e) {

			log.error("GitHub API response 파싱 실패 ", e);
			throw new IllegalStateException("Failed to parse GitHub API response", e);
		}
	}
}
