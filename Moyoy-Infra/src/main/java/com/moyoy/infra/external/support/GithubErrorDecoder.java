package com.moyoy.infra.external.support;

import com.moyoy.domain.support.error.github.GithubResourceNotFoundException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class GithubErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String s, Response response) {

		return switch (response.status()) {
			case 404 -> new GithubResourceNotFoundException();
			default -> new IllegalStateException("Unexpected value: " + response.status());
		};
	}
}
