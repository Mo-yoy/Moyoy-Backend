package com.moyo.backend.follow.infrastructure;

import com.moyo.backend.follow.application.FollowRelationRepository;
import com.moyo.backend.follow.domain.FollowRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FollowRelationRedisRepositoryImpl implements FollowRelationRepository {

    private static final String FOLLOW_RELATION_KEY_PREFIX =  "follow_relation:";
    private static final long FOLLOW_RELATION_TTL_MINUTES = 15;
    private final RedisTemplate<String, FollowRelation> redisTemplate;

    @Override
    public Optional<FollowRelation> findByUserId(Long currentUserId) {

        String key = getFollowRelationKey(currentUserId);
        FollowRelation value = redisTemplate.opsForValue().get(key);

        return Optional.ofNullable(value);
    }

    @Override
    public void save(Long currentUserId, FollowRelation followRelation) {

        String key = getFollowRelationKey(currentUserId);
        redisTemplate.opsForValue().set(key, followRelation, Duration.ofMinutes(FOLLOW_RELATION_TTL_MINUTES));
    }

    private String getFollowRelationKey(Long currentUserId){
        return FOLLOW_RELATION_KEY_PREFIX + currentUserId;
    }
}
