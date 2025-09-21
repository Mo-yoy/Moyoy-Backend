package com.moyoy.api.auth.security.component;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.moyoy.infra.database.mysql.support.AESEncryptor;

@Component
public class RdbOAuth2AuthorizedClientService extends JdbcOAuth2AuthorizedClientService {

	private final AESEncryptor encryptService;

	public RdbOAuth2AuthorizedClientService(
		JdbcOperations jdbcOperations,
		ClientRegistrationRepository clientRegistrationRepository,
		AESEncryptor aesEncryptor) {
		super(jdbcOperations, clientRegistrationRepository);
		this.encryptService = aesEncryptor;

		// 저장/업데이트: 토큰을 암호화해서 BLOB로 저장
		setAuthorizedClientParametersMapper(holder -> {
			OAuth2AuthorizedClient c = holder.getAuthorizedClient();
			Authentication p = holder.getPrincipal();

			List<SqlParameterValue> params = new ArrayList<>();
			params.add(new SqlParameterValue(Types.VARCHAR, c.getClientRegistration().getRegistrationId()));
			params.add(new SqlParameterValue(Types.VARCHAR, p.getName()));
			params.add(new SqlParameterValue(Types.VARCHAR, c.getAccessToken().getTokenType().getValue()));

			byte[] atBytes = c.getAccessToken().getTokenValue().getBytes(StandardCharsets.UTF_8);
			params.add(new SqlParameterValue(Types.BLOB, encryptService.encrypt(atBytes)));

			params.add(new SqlParameterValue(Types.TIMESTAMP, Timestamp.from(c.getAccessToken().getIssuedAt())));
			params.add(new SqlParameterValue(Types.TIMESTAMP, Timestamp.from(c.getAccessToken().getExpiresAt())));

			String scopes = CollectionUtils.isEmpty(c.getAccessToken().getScopes())
				? null
				: StringUtils.collectionToDelimitedString(c.getAccessToken().getScopes(), ",");
			params.add(new SqlParameterValue(Types.VARCHAR, scopes));

			OAuth2RefreshToken rt = c.getRefreshToken();
			if (rt != null) {
				byte[] rtBytes = rt.getTokenValue().getBytes(StandardCharsets.UTF_8);
				params.add(new SqlParameterValue(Types.BLOB, encryptService.encrypt(rtBytes)));
				params.add(new SqlParameterValue(Types.TIMESTAMP,
					rt.getIssuedAt() == null ? null : Timestamp.from(rt.getIssuedAt())));
			} else {
				params.add(new SqlParameterValue(Types.BLOB, null));
				params.add(new SqlParameterValue(Types.TIMESTAMP, null));
			}
			return params;
		});

		// 조회: BLOB에서 읽어 복호화
		setAuthorizedClientRowMapper(new DecryptingRowMapper(clientRegistrationRepository));
	}

	private class DecryptingRowMapper implements RowMapper<OAuth2AuthorizedClient> {
		private final ClientRegistrationRepository repo;

		DecryptingRowMapper(ClientRegistrationRepository repo) {
			this.repo = repo;
		}

		@Override
		public OAuth2AuthorizedClient mapRow(ResultSet rs, int rowNum) throws SQLException {
			String regId = rs.getString("client_registration_id");
			ClientRegistration cr = repo.findByRegistrationId(regId);
			if (cr == null) {
				throw new DataRetrievalFailureException("ClientRegistration not found: " + regId);
			}

			OAuth2AccessToken.TokenType type = OAuth2AccessToken.TokenType.BEARER.getValue()
				.equalsIgnoreCase(rs.getString("access_token_type"))
					? OAuth2AccessToken.TokenType.BEARER : null;

			// access token
			byte[] atEnc = rs.getBytes("access_token_value");
			if (atEnc == null)
				throw new DataRetrievalFailureException("access_token_value is null");
			String at = new String(encryptService.decrypt(atEnc), StandardCharsets.UTF_8);

			Instant issued = rs.getTimestamp("access_token_issued_at").toInstant();
			Instant expires = rs.getTimestamp("access_token_expires_at").toInstant();
			Set<String> scopes = Optional.ofNullable(rs.getString("access_token_scopes"))
				.map(StringUtils::commaDelimitedListToSet)
				.orElse(Collections.emptySet());
			OAuth2AccessToken access = new OAuth2AccessToken(type, at, issued, expires, scopes);

			// refresh token
			OAuth2RefreshToken refresh = null;
			byte[] rtEnc = rs.getBytes("refresh_token_value");
			if (rtEnc != null) {
				String rt = new String(encryptService.decrypt(rtEnc), StandardCharsets.UTF_8);
				Timestamp rts = rs.getTimestamp("refresh_token_issued_at");
				refresh = new OAuth2RefreshToken(rt, rts == null ? null : rts.toInstant());
			}

			String principal = rs.getString("principal_name");
			return new OAuth2AuthorizedClient(cr, principal, access, refresh);
		}
	}
}
