package com.moyoy.api.auth.jwt.application;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.api.auth.jwt.support.JwtPayloadExtractor;
import com.moyoy.api.auth.jwt.support.JwtProvider;
import com.moyoy.api.auth.jwt.support.JwtRefreshWhiteListUpdater;
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

	public ReissueJwtResult reIssueJwt(String jwtRefreshRawToken) {

		jwtValidator.validate(JwtType.REFRESH, jwtRefreshRawToken);

		JwtUserInfo jwtUserInfo = jwtPayloadExtractor.extractUserInfo(jwtRefreshRawToken);

		String reIssueRefreshToken = jwtProvider.createJwtToken(jwtUserInfo, JwtType.REFRESH);
		String reIssueAccessToken = jwtProvider.createJwtToken(jwtUserInfo, JwtType.ACCESS);

		jwtRefreshWhiteListUpdater.updateRefreshTokenWhiteList(jwtUserInfo.userId(), jwtRefreshRawToken, reIssueRefreshToken);

		return new ReissueJwtResult(reIssueAccessToken, reIssueRefreshToken);
	}
}
