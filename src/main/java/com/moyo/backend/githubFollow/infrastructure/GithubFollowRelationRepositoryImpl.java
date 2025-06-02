package com.moyo.backend.githubFollow.infrastructure;

import com.moyo.backend.githubFollow.domain.GithubFollowRelation;
import com.moyo.backend.githubFollow.domain.GithubFollowRelationRepository;
import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import com.moyo.backend.githubFollow.exception.GithubRateLimitExceedException;
import com.moyo.backend.security.oauth.GithubOAuthTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;
import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_MIN_REQUEST_THRESHOLD;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowRelationRepositoryImpl implements GithubFollowRelationRepository {

    private final GithubFollowHttpClient githubFollowHttpClient;
    private final GithubOAuthTokenProvider githubOAuthTokenProvider;

    @Override
    @Caching(
            cacheable = @Cacheable(value = "followRelation", key = "#userId", condition = "!#forceSync"),
            put = @CachePut(value = "followRelation", key = "#userId", condition = "#forceSync")
    )
    public GithubFollowRelation findByUserId(Long userId, boolean forceSync) {

        String githubAccessToken = githubOAuthTokenProvider.getGithubAccessToken(userId);
        ResponseEntity<GithubUserFollowStats> response = githubFollowHttpClient.fetchFollowStatsByUserId(userId, githubAccessToken);

        GithubUserFollowStats followStats = response.getBody();
        int remainingRequestCnt = Integer.parseInt(response.getHeaders().get("X-RateLimit-Remaining").getFirst());
        int requiredRequestCnt = followStats.getTotalRequestCnt();
        if (remainingRequestCnt < requiredRequestCnt + GITHUB_MIN_REQUEST_THRESHOLD) throw new GithubRateLimitExceedException();

        log.info("{}의 팔로워, 팔로잉 페이지 정보 로그 (page size = {}) | followerMaxPage : {} , followingMaxPage : {}, 남은 요청 가능 횟수 : {}",
                userId, GITHUB_FOLLOW_QUERY_PAGING_SIZE, followStats.getMaxFollowerPageSize(), followStats.getMaxFollowingPageSize(), remainingRequestCnt);

        List<GithubFollowUser> githubFollowers = new ArrayList<>();
        List<GithubFollowUser> githubFollowings = new ArrayList<>();

        int maxFollowerPageSize = followStats.getMaxFollowerPageSize();
        int maxFollowingPageSize = followStats.getMaxFollowingPageSize();

        long startTime = System.currentTimeMillis();    // 삭제 예정

        // 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
        for (int currentPage = 1; currentPage <= maxFollowerPageSize; currentPage++) {

            githubFollowers.addAll(githubFollowHttpClient.fetchPagedFollowers(currentPage, githubAccessToken));
        }

        for (int currentPage = 1; currentPage <= maxFollowingPageSize; currentPage++) {

            githubFollowings.addAll(githubFollowHttpClient.fetchPagedFollowings(currentPage, githubAccessToken));
        }

        log.info("[개발용 로그] 동기식 API 요청 소요 시간 : {}",System.currentTimeMillis() - startTime); // 삭제 예정

        return GithubFollowRelation.create(userId, githubFollowers, githubFollowings);
    }

}
