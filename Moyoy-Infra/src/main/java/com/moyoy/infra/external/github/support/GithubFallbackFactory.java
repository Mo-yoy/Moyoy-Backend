package com.moyoy.infra.external.github.support;

import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.moyoy.infra.external.github.support.error.GithubServerUnavailableException;
import com.moyoy.infra.external.github.support.error.GithubUnknownErrorException;

import com.moyoy.common.error.MoyoException;

import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

@Slf4j
@Component
public class GithubFallbackFactory<T> implements FallbackFactory<T> {

	@Override
	public T create(Throwable cause) {

		if (cause instanceof CallNotPermittedException) {
			log.warn("CircuitBreaker OPEN - 깃허브 API 호출 차단됨: {}", cause.getMessage());
			throw new GithubServerUnavailableException();
		}

		if (cause instanceof RetryableException || cause instanceof TimeoutException) {
			log.warn("Github API 요청중 네트워크 에러 발생: {}", cause.getMessage());
			throw new GithubServerUnavailableException();
		}

		if (cause instanceof MoyoException) {
			throw (MoyoException)cause;
		}

		log.error("깃허브 API 호출중 알수 없는 오류 발생 : {}", cause.getMessage());
		throw new GithubUnknownErrorException(500, cause);
	}
}
