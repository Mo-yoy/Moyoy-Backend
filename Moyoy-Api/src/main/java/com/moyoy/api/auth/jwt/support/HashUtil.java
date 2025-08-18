package com.moyoy.api.auth.jwt.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class HashUtil {
	private HashUtil() {}

	public static String sha256Base64(String raw) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(digest);
		} catch (Exception e) {
			throw new IllegalStateException("SHA-256 해시중 에러 발생", e);
		}
	}
}
