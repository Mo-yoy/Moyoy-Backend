package com.moyoy.infra.external.github.support.error;

public class GithubPollingApiTimeOutException extends Exception {
	public GithubPollingApiTimeOutException() {
		super("Github Polling Api TimeOut");
	}
}
