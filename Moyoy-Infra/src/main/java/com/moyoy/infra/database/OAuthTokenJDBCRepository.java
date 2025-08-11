package com.moyoy.infra.database;

import static java.nio.charset.StandardCharsets.*;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OAuthTokenJDBCRepository {

	private final JdbcTemplate jdbc;

	public Optional<String> findToken(String registrationId, String principalName) {
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
				byte[] blob = rs.getBytes("access_token_value");

				if (blob == null) return Optional.empty();
				String value = new String(blob, UTF_8);

				return Optional.of(type + " " + value);
			}
		);
	}

}
