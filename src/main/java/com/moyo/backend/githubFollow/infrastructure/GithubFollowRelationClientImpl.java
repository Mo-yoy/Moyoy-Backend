package com.moyo.backend.githubFollow.infrastructure;

import com.moyo.backend.githubFollow.domain.GithubFollowRelation;
import com.moyo.backend.githubFollow.domain.GithubFollowRelationClient;
import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import com.moyo.backend.githubFollow.exception.GithubRateLimitRemainingExceedException;
import com.moyo.backend.user.User;
import com.moyo.backend.user.UserRepository;
import com.moyo.backend.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowRelationClientImpl implements GithubFollowRelationClient {


    private final UserRepository userRepository;
    private final GithubFollowApiClient githubFollowApiClient;
    private final CacheService cacheService;

    @Cacheable(value = "followRelation", key = "#userId")
    @Override
    public GithubFollowRelation load(Long userId, String username, String accessToken) {

        GithubFollowDetectInfo detectInfo = githubFollowApiClient.fetchFollowDetectInfo(username, accessToken);
        logDetectInfo(userId, detectInfo);

        if (detectInfo.canFollowFetchRequest()) {

            List<GithubFollowUser> githubFollowers = new ArrayList<>();
            List<GithubFollowUser> githubFollowings = new ArrayList<>();

            long startTime = System.currentTimeMillis();

            // 추후 비동기로 개선할 성능 장애 지점
            for (int currentPage = 1; currentPage <= detectInfo.getMaxFollowerPage(); currentPage++) {

                githubFollowers.addAll(githubFollowApiClient.getFollowerList(currentPage, accessToken));
            }

            for (int currentPage = 1; currentPage <= detectInfo.getMaxFollowingPage(); currentPage++) {

                githubFollowings.addAll(githubFollowApiClient.getFollowingList(currentPage, accessToken));
            }

            log.info("[개발용 로그] 동기식 API 요청 소요 시간 : {}",System.currentTimeMillis() - startTime);

            return GithubFollowRelation.builder()
                    .userId(userId)
                    .githubFollowers(githubFollowers)
                    .githubFollowings(githubFollowings)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        else throw new GithubRateLimitRemainingExceedException();
    }

    @Override
    public void follow(Long currentUserId, Long targetUserId, String accessToken) {

        GithubFollowUser targetUser = githubFollowApiClient.getFollowUserInfo(targetUserId,accessToken);
        log.info("깃허브 팔로우 요청 | 요청자 : {} -> [{}, {}]", currentUserId, targetUser.id(), targetUser.username());

        int responseStatus = githubFollowApiClient.follow(targetUser.username(), accessToken);

        if(responseStatus == 204){

            cacheService.addFollowingToCurrentUser(currentUserId, targetUser);

            // 팔로우 당하는 타겟 유저가 우리 서비스의 유저인지 체크
            if(userRepository.existsById(targetUser.id())) {

                User user = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
                GithubFollowUser currentUser = new GithubFollowUser(user.getId(), user.getUsername(), user.getProfileImgUrl());
                cacheService.addFollowerToTargetUser(targetUserId, currentUser);
            }

        }
        else throw new RuntimeException("깃허브 팔로우 중 에러 발생"); // 에러 처리 계획중
    }

    @Override
    public void unfollow(Long currentUserId, Long targetUserId, String accessToken) {

        GithubFollowUser targetUser = githubFollowApiClient.getFollowUserInfo(targetUserId,accessToken);
        log.info("깃허브 언 팔로우 요청 | 요청자 : {} -> [{}, {}]", currentUserId, targetUser.id(), targetUser.username());

        int responseStatus = githubFollowApiClient.unfollow(targetUser.username(), accessToken);

        if(responseStatus == 204){

            cacheService.deleteFollowingToCurrentUser(currentUserId, targetUserId);

            // 언 팔로우 당하는 타겟 유저가 우리 서비스의 유저인지 체크
            if(userRepository.existsById(targetUser.id())) cacheService.deleteFollowerToTargetUser(currentUserId, targetUserId);

        }
        else throw new RuntimeException("깃허브 언 팔로우 중 에러 발생"); // 에러 처리 계획중
    }


    // 개발용 임시 로그
    private void logDetectInfo(Long userId, GithubFollowDetectInfo followDetectInfo) {

        log.info("{}의 팔로워, 팔로잉 페이지 정보 로그 (page size = {}) | followerMaxPage : {} , followingMaxPage : {}",
                userId,
                GITHUB_FOLLOW_QUERY_PAGING_SIZE,
                followDetectInfo.getMaxFollowerPage(),
                followDetectInfo.getMaxFollowingPage()
        );

        log.info("{}의 남은 요청 수 : ({}/5000)", userId, followDetectInfo.getRateLimitRemaining());
    }

}
