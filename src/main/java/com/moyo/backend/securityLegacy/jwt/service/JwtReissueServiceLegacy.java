package com.moyo.backend.securityLegacy.jwt.service;

import com.moyo.backend.securityLegacy.jwt.util.JwtPayloadReader;
import com.moyo.backend.securityLegacy.jwt.util.JwtProvider;
import com.moyo.backend.securityLegacy.jwt.util.JwtValidator;
import com.moyo.backend.securityLegacy.oauth.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.moyo.backend.common.constant.MoyoConstants.*;

@Service
@RequiredArgsConstructor
public class JwtReissueServiceLegacy {

    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final JwtPayloadReader jwtPayloadReader;
    private final LoginRepository redisRepository;
    public Map<String, String> reIssueJwt(String jwtRefreshToken) {

        // 리프레시 토큰 검증
        jwtValidator.validateJwtRefreshToken(jwtRefreshToken);

        // 화이트 리스트에 토큰이 없다면 차단 처리
        Long userId = jwtPayloadReader.getUserId(jwtRefreshToken);
        String whiteListTokenKey = redisRepository.findWhiteListTokenKey(userId, jwtRefreshToken);
        if(whiteListTokenKey==null) throw new RuntimeException("차단된 리프레시 토큰 입니다.");

        // 유효한 토큰이고 화이트 리스트에 등록된 토큰이라면 기존의 refresh를 이용해 jwtaccess, jwtrefresh 재발급
        String role = jwtPayloadReader.getRole(jwtRefreshToken);
        String newAccess = jwtProvider.createJwtAccess(userId,role);
        String newRefresh = jwtProvider.createJwtRefresh(userId,role);

        // RTR
        redisRepository.delete(whiteListTokenKey);
        redisRepository.save(userId,newRefresh,jwtPayloadReader.getExpiration(newRefresh));

        return Map.of(JWT_ACCESS_TYPE, newAccess, JWT_REFRESH_TYPE, newRefresh);
    }
}

