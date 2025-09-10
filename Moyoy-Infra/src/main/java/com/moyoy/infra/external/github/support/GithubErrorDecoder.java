package com.moyoy.infra.external.github.support;

import com.moyoy.infra.external.github.support.error.GithubClientErrorException;
import com.moyoy.infra.external.github.support.error.GithubForbiddenException;
import com.moyoy.infra.external.github.support.error.GithubResourceNotFoundException;
import com.moyoy.infra.external.github.support.error.GithubServerErrorException;
import com.moyoy.infra.external.github.support.error.GithubUnauthorizedException;
import com.moyoy.infra.external.github.support.error.GithubUnknownErrorException;
import com.moyoy.infra.external.github.support.error.GithubValidationFailedException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class GithubErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {

		int status = response.status();

		return switch (status) {
			case 401 -> new GithubUnauthorizedException();
			case 403 -> new GithubForbiddenException();
			case 404 -> new GithubResourceNotFoundException();
			case 422 -> new GithubValidationFailedException();
			default -> {
				if (status >= 400 && status < 500) {
					yield new GithubClientErrorException(status);
				} else if (status >= 500 && status < 600) {
					yield new GithubServerErrorException(status);
				} else {
					yield new GithubUnknownErrorException(status);
				}
			}
		};
	}
}
