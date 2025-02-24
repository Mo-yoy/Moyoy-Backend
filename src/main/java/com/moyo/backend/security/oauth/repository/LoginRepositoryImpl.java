package com.moyo.backend.security.oauth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class LoginRepositoryImpl implements LoginRepository{

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String providerId, String refreshToken, Date expiration) {

        String memberKey = "members:" + providerId;

        // idx 필드를 관리하고, 새 토큰을 저장
        Long idx = redisTemplate.opsForHash().increment(memberKey, "idx", 1); // idx 값을 증가시킴

        // 고유한 토큰 키
        String tokenKey = memberKey + ":token:" + idx;

        // ttl 계산
        long ttl = (expiration.getTime() - System.currentTimeMillis() + 100)/1000;

        redisTemplate.opsForValue().set(tokenKey,refreshToken,ttl, TimeUnit.SECONDS);
    }


    @Override
    public String findWhiteListTokenKey(String providerId, String jwtRefreshToken){

        // 화이트리스트에 있는 토큰이 맞는지 체크
        String whiteListRefreshTokenKey = null;

        // 특정 회원의 토큰 패턴: "members:{memberId}:token:*"
        String memberKeyPattern = "members:" + providerId + ":token:*";

        // SCAN 옵션 설정: 해당 패턴과 일치하는 키를 찾음
        ScanOptions options = ScanOptions.scanOptions().match(memberKeyPattern).build();
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options);

        try {
            while (cursor.hasNext()) {
                String tokenKey = new String(cursor.next());
                String token = redisTemplate.opsForValue().get(tokenKey);  // 키에 저장된 토큰값을 가져옴

                if(token != null && token.equals(jwtRefreshToken)){
                    whiteListRefreshTokenKey = tokenKey;
                    break;
                }
            }
        } finally {
            cursor.close(); // 커서 닫기
        }

        return whiteListRefreshTokenKey;
    }

    @Override
    public void delete(String key){
        redisTemplate.delete(key);
    }
}

