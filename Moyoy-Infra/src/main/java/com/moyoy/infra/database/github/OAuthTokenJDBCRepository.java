package com.moyoy.infra.database.github;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.moyoy.infra.external.github.common.AESEncryptor;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OAuthTokenJDBCRepository implements OAuthTokenRepository {

	private final JdbcTemplate jdbc;
	private final AESEncryptor encryptor;

	@Override
	public Optional<String> findAccessToken(String registrationId, String principalName) {

		return jdbc.query(
			"""
			SELECT access_token_type, access_token_value
			FROM oauth2_authorized_client
			WHERE client_registration_id = ? AND principal_name = ?
			""",
			ps -> {
				ps.setString(1, registrationId);
				ps.setString(2, principalName);
			},
			rs -> {
				if (!rs.next()) return Optional.empty();

				String type = rs.getString("access_token_type");
				byte[] encryptedTokenValue = rs.getBytes("access_token_value");
				if (encryptedTokenValue == null) return Optional.empty();

				byte[] decryptedTokenValue = encryptor.decrypt(encryptedTokenValue);
				String tokenValue = new String(decryptedTokenValue, StandardCharsets.UTF_8);

				return Optional.of(type + " " + tokenValue);
			}
		);
	}
}
