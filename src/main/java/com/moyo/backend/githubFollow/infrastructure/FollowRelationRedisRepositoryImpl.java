package com.moyo.backend.githubFollow.infrastructure;

import com.moyo.backend.githubFollow.application.FollowRelationRepository;
import com.moyo.backend.githubFollow.domain.GithubFollowRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class FollowRelationRedisRepositoryImpl implements FollowRelationRepository {

    private static final String FOLLOW_RELATION_KEY_PREFIX =  "follow_relation:";
    private static final long FOLLOW_RELATION_TTL_MINUTES = 15;
    private final RedisTemplate<String, GithubFollowRelation> redisTemplate;

    @Override
    public Optional<GithubFollowRelation> findByUserId(Long currentUserId) {

        String key = getFollowRelationKey(currentUserId);
        GithubFollowRelation value = redisTemplate.opsForValue().get(key);

        return Optional.ofNullable(value);
    }

    @Override
    public void save(Long currentUserId, GithubFollowRelation githubFollowRelation) {

        String key = getFollowRelationKey(currentUserId);
        redisTemplate.opsForValue().set(key, githubFollowRelation, Duration.ofMinutes(FOLLOW_RELATION_TTL_MINUTES));
    }

    @Override
    public void clearFollowCache(Long currentUserId) {

        String key = getFollowRelationKey(currentUserId);
        redisTemplate.delete(key);
    }

    @Override
    public void update(Long currentUserId, GithubFollowRelation githubFollowRelation) {

        String key = getFollowRelationKey(currentUserId);

        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(key, githubFollowRelation);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);   // ttl 유지
    }

    private String getFollowRelationKey(Long currentUserId){
        return FOLLOW_RELATION_KEY_PREFIX + currentUserId;
    }
}
