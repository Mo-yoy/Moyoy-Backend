package com.moyoy.api.auth.jwt.support;

import java.sql.Timestamp;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JwtRefreshTokenJDBCRepository implements JwtRefreshTokenRepository {

	private final JdbcOperations jdbcOperations;

	private static final String INSERT_SQL = """
        INSERT INTO jwt_refresh_token (user_id, token_hash, expires_at)
        VALUES (?, ?, ?)
        """;

	private static final String DELETE_SQL = """
        DELETE FROM jwt_refresh_token
        WHERE token_hash = ?
        """;

	private static final String EXISTS_SQL = """
        SELECT EXISTS (
            SELECT 1
            FROM jwt_refresh_token
            WHERE token_hash = ?
        )
        """;

	@Override
	public void save(JwtRefreshTokenEntity jwtRefreshTokenEntity) {

		jdbcOperations.update(INSERT_SQL, ps -> {
			ps.setLong(1, jwtRefreshTokenEntity.getUserId());
			ps.setString(2, jwtRefreshTokenEntity.getTokenHash());
			ps.setTimestamp(3, Timestamp.valueOf(jwtRefreshTokenEntity.getExpiresAt()));
		});
	}

	@Override
	public boolean existByTokenHash(String tokenHash) {

		Boolean exists = jdbcOperations.queryForObject(EXISTS_SQL, Boolean.class, tokenHash);
		return exists != null && exists;
	}

	@Override
	public void deleteByTokenHash(String tokenHash) {
		jdbcOperations.update(DELETE_SQL, ps -> ps.setString(1, tokenHash));
	}
}
