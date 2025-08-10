package com.moyoy.api.auth.jwt.business;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.api.auth.jwt.implement.JwtPayloadExtractor;
import com.moyoy.api.auth.jwt.implement.JwtProvider;
import com.moyoy.api.auth.jwt.implement.JwtRefreshTokenValidator;
import com.moyoy.api.auth.jwt.implement.JwtUserInfo;
import com.moyoy.domain.auth.implement.JwtRefreshWhiteListUpdater;

@Service
@RequiredArgsConstructor
public class JwtReissueService {

	private final JwtProvider jwtProvider;
	private final JwtPayloadExtractor jwtPayloadExtractor;
	private final JwtRefreshTokenValidator jwtRefreshTokenValidator;
	private final JwtRefreshWhiteListUpdater jwtRefreshWhiteListUpdater;

	public ReissuedTokens reIssueJwt(String jwtRefreshToken) {

		jwtRefreshTokenValidator.validate(jwtRefreshToken);

		JwtUserInfo jwtUserInfo = jwtPayloadExtractor.extractUserInfo(jwtRefreshToken);

		String reIssueRefreshToken = jwtProvider.createJwtToken(jwtUserInfo, JWT_REFRESH_TYPE);
		String reIssueAccessToken = jwtProvider.createJwtToken(jwtUserInfo, JWT_ACCESS_TYPE);

		jwtRefreshWhiteListUpdater.updateRefreshTokenWhiteList(jwtRefreshToken, reIssueRefreshToken);

		return new ReissuedTokens(reIssueAccessToken, reIssueRefreshToken);
	}
}
