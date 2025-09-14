package com.moyoy.infra.external.github.support;

import java.util.function.Predicate;

import feign.FeignException;
import feign.RetryableException;

public class GithubExceptionRecordFailurePredicate implements Predicate<Throwable> {

	@Override
	public boolean test(Throwable throwable) {

		if (throwable instanceof RetryableException) {
			return true;
		}

		return throwable instanceof FeignException.FeignServerException;
	}
}
