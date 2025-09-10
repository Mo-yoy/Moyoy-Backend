package com.moyoy.infra.database.mysql.query;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.moyoy.infra.database.mysql.query.port.GithubTokenReader;
import com.moyoy.infra.database.mysql.support.AESEncryptor;

@Repository
@RequiredArgsConstructor
class GithubTokenJDBCReader implements GithubTokenReader {

	private final JdbcTemplate jdbc;
	private final AESEncryptor encryptor;

	@Override
	public Optional<String> findAccessTokenWithTokenType(Long userId) {

		return jdbc.query(
			"""
				SELECT access_token_type, access_token_value
				FROM oauth2_authorized_client
				WHERE client_registration_id = ? AND principal_name = ?
				""",
			ps -> {
				ps.setString(1, GITHUB_REGISTRATION_ID);
				ps.setString(2, userId.toString());
			},
			rs -> {
				if (!rs.next())
					return Optional.empty();

				String type = rs.getString("access_token_type");
				byte[] encryptedTokenValue = rs.getBytes("access_token_value");
				if (encryptedTokenValue == null)
					return Optional.empty();

				byte[] decryptedTokenValue = encryptor.decrypt(encryptedTokenValue);
				String tokenValue = new String(decryptedTokenValue, StandardCharsets.UTF_8);

				return Optional.of(type + " " + tokenValue);
			});
	}
}
