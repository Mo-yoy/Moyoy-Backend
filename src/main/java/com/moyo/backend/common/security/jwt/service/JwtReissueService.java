package com.moyo.backend.common.security.jwt.service;

import com.moyo.backend.common.security.jwt.util.JwtPayloadReader;
import com.moyo.backend.common.security.jwt.util.JwtProvider;
import com.moyo.backend.common.security.jwt.util.JwtValidator;
import com.moyo.backend.common.security.oauth.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.moyo.backend.common.constant.MoyoConstants.ACCESS_TYPE;
import static com.moyo.backend.common.constant.MoyoConstants.REFRESH_TYPE;

@Service
@RequiredArgsConstructor
public class JwtReissueService {

    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final JwtPayloadReader jwtPayloadReader;
    private final LoginRepository redisRepository;
    public Map<String, String> reIssueJwt(String jwtRefreshToken) {

        // 리프레시 토큰 검증
        jwtValidator.validateJwtRefreshToken(jwtRefreshToken);

        // 화이트리스트에 토큰이 없다면 차단 처리
        String providerId = jwtPayloadReader.getProviderId(jwtRefreshToken);
        String whiteListTokenKey = redisRepository.findWhiteListTokenKey(providerId, jwtRefreshToken);
        if(whiteListTokenKey==null) throw new RuntimeException("차단된 리프레시토큰 입니다.");

        // 유효한 토큰이고 화이트 리스트에 등록된 토큰이라면 기존의 refresh를 이용해 jwtaccess, jwtrefresh 재발급
        String role = jwtPayloadReader.getRole(jwtRefreshToken);
        String newAccess = jwtProvider.createJwtAccess(providerId,role);
        String newRefresh = jwtProvider.createJwtRefresh(providerId,role);

        // RTR
        redisRepository.delete(whiteListTokenKey);
        redisRepository.save(providerId,newRefresh,jwtPayloadReader.getExpiration(newRefresh));

        return Map.of(ACCESS_TYPE, newAccess, REFRESH_TYPE, newRefresh);
    }
}

