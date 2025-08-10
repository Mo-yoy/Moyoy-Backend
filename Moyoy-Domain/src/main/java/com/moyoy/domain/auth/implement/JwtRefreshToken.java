package com.moyoy.domain.auth.implement;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "jwt_refresh_token")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtRefreshToken {

	@Id
	@Column(name = "jwt_refresh_token_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 500, nullable = false, unique = true)
	private String value;

	private LocalDateTime expiresAt;

	@Builder
	private JwtRefreshToken(String value, LocalDateTime expiresAt) {
		this.value = value;
		this.expiresAt = expiresAt;
	}

	public static JwtRefreshToken from(JWTClaimsSet claimsSet, String jwtRefreshToken) {

		Date jwtExp = (Date)claimsSet.getClaim(JWT_CLAIM_EXPIRATION);
		LocalDateTime expiresAt = LocalDateTime.ofInstant(jwtExp.toInstant(), ZoneId.systemDefault());

		return JwtRefreshToken.builder()
			.value(jwtRefreshToken)
			.expiresAt(expiresAt)
			.build();
	}

}
