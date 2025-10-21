package com.moyoy.infra.redis.cache.github_follow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.redisson.api.FunctionMode;
import org.redisson.api.FunctionResult;
import org.redisson.api.RFunction;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowSnapshotUpdater {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;
    private final PropertiesLoaderSupport propertiesLoaderSupport;

    public void addFollowingToSnapshot(Long userId, Supplier<GithubUserProfile> targetUserProfile){

        try {
            String key = getSnapshotKey(userId);
            GithubUserProfile targetProfile = targetUserProfile.get();
            String profileJson = objectMapper.writeValueAsString(targetProfile);

            RFunction function = redissonClient.getFunction();
            Boolean result = function.call(
                    FunctionMode.WRITE,
                    "add_following",
                    FunctionResult.BOOLEAN,
                    Collections.singletonList(key),
                    profileJson
            );

            if (result == null || !result) { throw new RuntimeException("캐시 업데이트 실패");}
        }
        catch (Exception e) {
            throw new RuntimeException("캐시 업데이트 실패", e); // TODO 추후 처리
        }
    }

    public void addFollowerToSnapshot(){

    }

    public void removeFollowingFromSnapshot(){

    }

    public void removeFollowerFromSnapshot(){}

    private String getSnapshotKey(Long userId) {
        return String.format("followSnapshot::%s", userId);
    }
}
