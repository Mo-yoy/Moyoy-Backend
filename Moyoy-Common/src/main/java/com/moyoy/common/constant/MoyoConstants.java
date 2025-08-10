package com.moyoy.common.constant;

public class MoyoConstants {

	// HTTP Header 
	public static final String SET_COOKIE = "Set-Cookie";
	public static final String AUTHORIZATION = "Authorization";
	public static final String JSON = "application/json";
	public static final String UTF_8 = "UTF-8";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String ACCEPT = "Accept";

	// HTTP 상태 코드
	public static final int OK = 200;
	public static final int NO_CONTENT = 204;
	public static final int BAD_REQUEST = 400;
	public static final int UNAUTHORIZED = 401;
	public static final int FORBIDDEN = 403;
	public static final int NOT_FOUND = 404;
	public static final int METHOD_NOT_ALLOWED = 405;
	public static final int SERVER_ERROR = 500;

	// JWT
	public static final String JWT_ACCESS_TYPE = "access";
	public static final String JWT_REFRESH_TYPE = "refresh";
	public static final String JWT_CLAIM_USER_ID = "id";
	public static final String JWT_CLAIM_TOKEN_TYPE = "type";
	public static final String JWT_CLAIM_AUTHORITY = "authority";
	public static final String JWT_CLAIM_EXPIRATION = "exp";
	public static final long JWT_ACCESS_TOKEN_EXPIRATION_MINUTE = 1800000L;
	public static final long JWT_REFRESH_TOKEN_EXPIRATION_MINUTE = 18000000L;

	// Github
	public static final String GITHUB_REGISTRATION_ID = "github";
	public static final String GITHUB_OAUTH2_USER_ID = "id";
	public static final String GITHUB_OAUTH2_USER_NAME = "login";
	public static final String GITHUB_OAUTH2_USER_AVATAR_URL = "avatar_url";
	public static final int GITHUB_QUERY_PAGING_SIZE = 100;

	public static final long ONE_MINUTE_MILLS = 60000L; // 60 * 1000

}
