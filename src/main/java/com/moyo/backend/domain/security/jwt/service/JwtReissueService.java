package com.moyo.backend.domain.security.jwt.service;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import java.text.ParseException;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moyo.backend.domain.security.jwt.domain.JwtRefreshToken;
import com.moyo.backend.domain.security.jwt.exception.JwtTokenBlockedException;
import com.moyo.backend.domain.security.jwt.exception.JwtTokenExpiredException;
import com.moyo.backend.domain.security.jwt.exception.JwtTokenInvalidException;
import com.moyo.backend.domain.security.jwt.exception.JwtTokenNotExistException;
import com.moyo.backend.domain.security.jwt.exception.JwtTokenTypeMismatchException;
import com.moyo.backend.domain.security.jwt.repository.JwtRefreshTokenRepository;
import com.moyo.backend.domain.security.jwt.util.JwtProvider;
import com.moyo.backend.domain.security.oauth.GithubOAuth2User;
import com.moyo.backend.security.jwt.exception.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JwtReissueService {

	private final OctetSequenceKey jwk;
	private final JwtProvider jwtProvider;
	private final MACVerifier macVerifier;
	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

	public Map<String, String> reIssueJwt(String jwtRefreshToken) {

		if (jwtRefreshToken.isEmpty() || jwtRefreshToken.isBlank())
			throw new JwtTokenNotExistException();

		String reissueAccess;
		String reissueRefresh;

		try {
			SignedJWT signedJWT = SignedJWT.parse(jwtRefreshToken);
			boolean verifyResult = signedJWT.verify(macVerifier);

			JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

			if (verifyResult) {
				String type = jwtClaimsSet.getClaim(JWT_CLAIM_TOKEN_TYPE).toString();
				if (type != null && !type.equals(JWT_REFRESH_TYPE))
					throw new JwtTokenTypeMismatchException();
				if (jwtClaimsSet.getExpirationTime() != null && jwtClaimsSet.getExpirationTime().before(new Date()))
					throw new JwtTokenExpiredException();
				if (!jwtRefreshTokenRepository.existByTokenValue(jwtRefreshToken))
					throw new JwtTokenBlockedException();

				String authority = jwtClaimsSet.getClaim(JWT_CLAIM_AUTHORITY).toString();
				Set<GrantedAuthority> authorities = new HashSet<>();
				authorities.add(new SimpleGrantedAuthority(authority));

				String id = jwtClaimsSet.getClaim(JWT_CLAIM_USER_ID).toString();
				Map<String, Object> attributes = new HashMap<>();
				attributes.put("id", id);

				GithubOAuth2User user = new GithubOAuth2User(authorities, attributes);

				reissueRefresh = jwtProvider.createJwtToken(user, jwk, JWT_REFRESH_TYPE);
				reissueAccess = jwtProvider.createJwtToken(user, jwk, JWT_ACCESS_TYPE);

				JwtRefreshToken reissuedRefreshToken = JwtRefreshToken.from(SignedJWT.parse(reissueRefresh).getJWTClaimsSet(), reissueRefresh);
				jwtRefreshTokenRepository.deleteByTokenValue(jwtRefreshToken);
				jwtRefreshTokenRepository.save(reissuedRefreshToken);
			} else
				throw new JwtTokenInvalidException();

		} catch (ParseException | JOSEException e) {
			log.error(e.toString());
			throw new JwtTokenInvalidException();
		}

		return Map.of(JWT_ACCESS_TYPE, reissueAccess, JWT_REFRESH_TYPE, reissueRefresh);
	}
}
