package com.moyoy.infra.external.github.support;

import com.moyoy.infra.external.github.support.error.GithubForbiddenException;
import com.moyoy.infra.external.github.support.error.GithubResourceNotFoundException;
import com.moyoy.infra.external.github.support.error.GithubUnauthorizedException;
import com.moyoy.infra.external.github.support.error.GithubValidationFailedException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class GithubErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		return switch (response.status()) {
			case 401 -> new GithubUnauthorizedException();
			case 403 -> new GithubForbiddenException();
			case 404 -> new GithubResourceNotFoundException();
			case 422 -> new GithubValidationFailedException();
			default -> new IllegalStateException("Unexpected status: " + response.status());
		};
	}
}
