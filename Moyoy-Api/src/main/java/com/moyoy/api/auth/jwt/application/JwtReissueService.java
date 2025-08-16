package com.moyoy.api.auth.jwt.application;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.api.auth.jwt.support.JwtPayloadExtractor;
import com.moyoy.api.auth.jwt.support.JwtProvider;
import com.moyoy.api.auth.jwt.legacy.JwtRefreshWhiteListUpdater;
import com.moyoy.api.auth.jwt.support.JwtType;
import com.moyoy.api.auth.jwt.support.JwtUserInfo;
import com.moyoy.api.auth.jwt.support.JwtValidator;

@Service
@RequiredArgsConstructor
public class JwtReissueService {

	private final JwtProvider jwtProvider;
	private final JwtPayloadExtractor jwtPayloadExtractor;
	private final JwtValidator jwtValidator;
	private final JwtRefreshWhiteListUpdater jwtRefreshWhiteListUpdater;

	public ReissuedTokens reIssueJwt(String jwtRefreshToken) {

		jwtValidator.validate(JwtType.REFRESH, jwtRefreshToken);

		JwtUserInfo jwtUserInfo = jwtPayloadExtractor.extractUserInfo(jwtRefreshToken);

		String reIssueRefreshToken = jwtProvider.createJwtToken(jwtUserInfo, JWT_REFRESH_TYPE);
		String reIssueAccessToken = jwtProvider.createJwtToken(jwtUserInfo, JWT_ACCESS_TYPE);

		jwtRefreshWhiteListUpdater.updateRefreshTokenWhiteList(jwtRefreshToken, reIssueRefreshToken);

		return new ReissuedTokens(reIssueAccessToken, reIssueRefreshToken);
	}
}
