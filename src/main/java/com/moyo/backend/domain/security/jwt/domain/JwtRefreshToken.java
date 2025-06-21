package com.moyo.backend.domain.security.jwt.domain;

import static com.moyo.backend.common.constant.MoyoConstants.JWT_CLAIM_EXPIRATION;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.persistence.*;

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

	@Builder(access = AccessLevel.PRIVATE)
	private JwtRefreshToken(String value, LocalDateTime expiresAt) {
		this.value = value;
		this.expiresAt = expiresAt;
	}

	public static JwtRefreshToken from(JWTClaimsSet claimsSet, String jwtRefreshToken) {

		return JwtRefreshToken.builder()
			.value(jwtRefreshToken)
			.expiresAt(LocalDateTime.ofInstant(((Date)claimsSet.getClaim(JWT_CLAIM_EXPIRATION)).toInstant(), ZoneId.systemDefault()))
			.build();
	}

}
