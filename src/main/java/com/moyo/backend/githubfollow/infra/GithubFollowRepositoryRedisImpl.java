package com.moyo.backend.githubfollow.infra;

import com.moyo.backend.githubfollow.model.FollowUser;
import com.moyo.backend.githubfollow.service.GithubFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class GithubFollowRepositoryRedisImpl implements GithubFollowRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, FollowUser> followUserRedisTemplate;

    @Override
    public boolean existByUserId(Long currentUserId) {

        String followCacheKey = currentUserId + ":follow:time";
        return stringRedisTemplate.hasKey(followCacheKey);
    }

    @Override
    public void saveFollowCreatedAt(Long currentUserId, LocalDateTime now) {
        String followCacheKey = currentUserId + ":follow:time";
        String followingListKey = currentUserId + ":follow:followings";
        String followerListKey = currentUserId + ":follow:followers";
        int ttl = 300;

        DefaultRedisScript<Void> script = new DefaultRedisScript<>();
        script.setScriptText(
                "redis.call('SET', KEYS[1], ARGV[1]) " +  // follow:time 저장
                        "redis.call('EXPIRE', KEYS[1], ARGV[2]) " + // TTL 설정
                        "redis.call('EXPIRE', KEYS[2], ARGV[2]) " + // TTL 동기화
                        "redis.call('EXPIRE', KEYS[3], ARGV[2]) "
        );
        script.setResultType(Void.class);

        List<String> keys = Arrays.asList(followCacheKey, followingListKey, followerListKey);
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        stringRedisTemplate.execute(script, keys, formattedDate, String.valueOf(ttl));
    }

    @Override
    public void saveFollowingList(Long currentUserId, List<FollowUser> followings) {

        String followingListKey = currentUserId + ":follow:followings";

        for(FollowUser user : followings) {
            followUserRedisTemplate.opsForZSet().add(followingListKey, user, user.getId());
        }
    }

    @Override
    public void saveFollowerList(Long currentUserId, List<FollowUser> followers) {

        String followerListKey = currentUserId + ":follow:followers";

        for(FollowUser user : followers) {
            followUserRedisTemplate.opsForZSet().add(followerListKey, user, user.getId());
        }
    }

    @Override
    public List<FollowUser> getFollowingList(Long currentUserId) {

        String followingListKey = currentUserId + ":follow:followings";

        Set<FollowUser> members = followUserRedisTemplate.opsForZSet().range(followingListKey, 0, -1);
        return new ArrayList<>(members);
    }

    @Override
    public List<FollowUser> getFollowerList(Long currentUserId) {

        String followingListKey = currentUserId + ":follow:followers";

        Set<FollowUser> members = followUserRedisTemplate.opsForZSet().range(followingListKey, 0, -1);
        return new ArrayList<>(members);
    }

    @Override
    public void deleteFollowCache(Long currentUserId) {

        String followCacheKey = currentUserId + ":follow:time";
        String followingListKey = currentUserId + ":follow:followings";
        String followerListKey = currentUserId + ":follow:followers";

        stringRedisTemplate.delete(followCacheKey);
        followUserRedisTemplate.delete(followingListKey);
        followUserRedisTemplate.delete(followerListKey);
    }

    @Override
    public void saveFollowing(Long currentUserId, FollowUser followUser) {

        String followingListKey = currentUserId + ":follow:followings";
        followUserRedisTemplate.opsForZSet().add(followingListKey, followUser, followUser.getId());
    }

    @Override
    public void saveFollower(Long targetUserId, FollowUser followUser) {

        String followerListKey = targetUserId + ":follow:followers";
        followUserRedisTemplate.opsForZSet().add(followerListKey, followUser, followUser.getId());
    }

    @Override
    public void deleteFollowingUser(Long currentUserId, Long targetUserId) {

        String followingListKey = currentUserId + ":follow:followings";

        // targetUserId를 score로 가지는 유저 한 명 가져오기
        FollowUser userToRemove = followUserRedisTemplate.opsForZSet()
                .rangeByScore(followingListKey, targetUserId, targetUserId)
                .stream()
                .findFirst()
                .orElse(null);

        followUserRedisTemplate.opsForZSet().remove(followingListKey, userToRemove);
    }

    @Override
    public void deleteFollowerUser(Long targetUserId, Long currentUserId) {

        String followerListKey = targetUserId + ":follow:followers";

        // currentUserId를 score로 가지는 FollowUser 찾기
        FollowUser userToRemove = followUserRedisTemplate.opsForZSet()
                .rangeByScore(followerListKey, currentUserId, currentUserId)
                .stream()
                .findFirst()
                .orElse(null);

        followUserRedisTemplate.opsForZSet().remove(followerListKey, userToRemove);
    }


}
