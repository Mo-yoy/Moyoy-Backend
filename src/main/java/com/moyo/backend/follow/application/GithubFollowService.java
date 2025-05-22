package com.moyo.backend.follow.application;

import com.moyo.backend.follow.domain.FollowRelation;
import com.moyo.backend.follow.domain.GithubFollowDetectInfo;
import com.moyo.backend.follow.dto.request.GithubFollowDetectRequest;
import com.moyo.backend.follow.dto.response.GithubFollowDetectResponse;
import com.moyo.backend.follow.dto.response.GithubFollowUserInfoResponse;
import com.moyo.backend.follow.exception.GithubRateLimitRemainingExceedException;
import com.moyo.backend.user.User;
import com.moyo.backend.user.UserRepository;
import com.moyo.backend.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;
import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubFollowService {

    private final UserRepository userRepository;
    private final GithubFollowApiClient githubFollowApiClient;
    private final FollowRelationRepository followRelationRepository;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public GithubFollowDetectResponse detectFollowUserList(Long currentUserId, GithubFollowDetectRequest request) {

        FollowRelation followRelation = getFollowRelationInCache(currentUserId);
        
        // 캐시 미스
        if(followRelation == null) {

            // 깃허브에 현재 사용자가 요청을 보낼 수 있는지 예비 요청을 보냄 (username, oauthAccessToken 필요)
            String currentUsername = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new).getUsername();
            String oauthAccessToken = getOAuthAccessToken(currentUserId);
            GithubFollowDetectInfo followDetectInfo = githubFollowApiClient.fetchFollowDetectInfo(currentUsername, oauthAccessToken);
            logFollowDetectInfo(currentUserId, followDetectInfo);   // 임시 로그

            // Github API 에서 FollowRelation 도메인 모델 조회
            if (followDetectInfo.canFollowFetchRequest()) {

                List<GithubFollowUserInfoResponse> followers = new ArrayList<>();
                List<GithubFollowUserInfoResponse> followings = new ArrayList<>();

                // 성능 장애 지점
                for (int currentPage = 1; currentPage <= followDetectInfo.getMaxFollowerPage(); currentPage++) {

                    followers.addAll(githubFollowApiClient.getFollowerList(oauthAccessToken, currentPage));
                }

                for (int currentPage = 1; currentPage <= followDetectInfo.getMaxFollowingPage(); currentPage++) {

                    followings.addAll(githubFollowApiClient.getFollowingList(oauthAccessToken, currentPage));
                }

                followRelation = FollowRelation.builder()
                        .userId(currentUserId)
                        .githubFollowers(followers)
                        .githubFollowings(followings)
                        .createdAt(LocalDateTime.now())
                        .build();
                
            } else throw new GithubRateLimitRemainingExceedException();


            try {
                followRelationRepository.save(currentUserId, followRelation);
            } catch (RedisConnectionFailureException ex) {
                log.warn("Redis Server Down");
            }
        }

        // FollowRelation 도메인 객체의 비즈니스 로직 수행
        List<GithubFollowUserInfoResponse> followUserList = followRelation.filterUsersByDetectType(request.getDetectType());

        // LastFetchedUserId를 기반 으로 Slice 처리 후 반환
        long lastFetchedId = request.getLastFetchedUserId();
        int pageSize = request.getPagingSize();

        // 불변 리스트 -> 가변 리스트 처리
        List<GithubFollowUserInfoResponse> pagingList = new ArrayList<>( 
                followUserList.stream()
                        .filter(user -> lastFetchedId == 0 || user.getId() > lastFetchedId)
                        .limit(pageSize + 1)
                        .toList()   
        );

        // Slice 처리
        boolean lastPage = pagingList.size() <= pageSize;
        if (!lastPage) pagingList.removeLast();

        // 캐시 히트일 경우, TTL이 분 단위를 넘지 않기 때문에 분으로 조회
        long minutes = Duration.between(followRelation.getCreatedAt(), LocalDateTime.now()).toMinutes()+1;

        return GithubFollowDetectResponse.builder()
                .userList(pagingList)
                .lastPage(lastPage)
                .totalUserCount(followUserList.size())
                .lastSyncAt(minutes + " 분전")
                .build();
    }

    public void clearFollowCache(Long currentUserId) {

        // 레디스 서버 다운으로 캐시 초기화를 진행 못해도 서버가 다운되면서 데이터가 날아가서 상관없이 정상 처리 하면 됨.
        try {
            followRelationRepository.clearFollowCache(currentUserId);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis Server Down");
        }
    }

    public void follow(Long currentUserId, Long targetUserId) {

        String oauthAccessToken = getOAuthAccessToken(currentUserId);

        // 팔로우 하고 싶은 깃허브 유저의 실제 깃허브 상 데이터를 가져옴.
        GithubFollowUserInfoResponse targetUser = githubFollowApiClient.getFollowUserInfo(targetUserId,oauthAccessToken);
        log.info("깃허브 팔로우 요청 | 요청자 : {} -> [{}, {}]", currentUserId, targetUser.getId(), targetUser.getUsername());

        int githubApiResponseStatus = githubFollowApiClient.follow(targetUser.getUsername(), oauthAccessToken);

        if(githubApiResponseStatus == 204){

            // 캐시에서 현재 팔로우를 요청한 사용자의 FollowRelation 도메인 모델 조회
            FollowRelation currentUserFollowRelation = getFollowRelationInCache(currentUserId);

            if (currentUserFollowRelation != null) {

                currentUserFollowRelation.addFollowing(targetUser);
                followRelationRepository.update(currentUserId, currentUserFollowRelation);
            }

            // 팔로우 당하는 타겟 유저가 우리 서비스의 유저인지 체크
            if(userRepository.existsById(targetUser.getId())) {

                // 타겟 유저의 캐시가 존재 하는지 확인
                FollowRelation targetUserFollowRelation = getFollowRelationInCache(targetUser.getId());

                if (targetUserFollowRelation != null) {

                    User user = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
                    GithubFollowUserInfoResponse currentUser = new GithubFollowUserInfoResponse(user.getId(), user.getUsername(), user.getProfileImgUrl());

                    targetUserFollowRelation.addFollower(currentUser);
                    followRelationRepository.update(targetUser.getId(), targetUserFollowRelation);
                }
            }

        }
        else throw new RuntimeException("깃허브 팔로우 중 에러 발생");  // 깃허브 API 명세서와 다른 응답이 오는거 같아서 조사중
    }

    public void unfollow(Long currentUserId, Long targetUserId) {

        String oauthAccessToken = getOAuthAccessToken(currentUserId);

        // 언 팔로우 하고 싶은 깃허브 유저의 실제 깃허브 상 데이터를 가져옴.
        GithubFollowUserInfoResponse targetUser = githubFollowApiClient.getFollowUserInfo(targetUserId,oauthAccessToken);
        log.info("깃허브 언 팔로우 요청 | 요청자 : {} -> [{}, {}]", currentUserId, targetUser.getId(), targetUser.getUsername());

        int githubApiResponseStatus = githubFollowApiClient.unfollow(targetUser.getUsername(), oauthAccessToken);

        if(githubApiResponseStatus == 204){

            // 캐시에서 현재 언 팔로우를 요청한 사용자의 FollowRelation 도메인 모델 조회
            FollowRelation currentUserFollowRelation = getFollowRelationInCache(currentUserId);

            if (currentUserFollowRelation != null) {

                currentUserFollowRelation.removeFollowing(targetUser.getId());
                followRelationRepository.update(currentUserId, currentUserFollowRelation);
            }

            // 언 팔로우 당하는 타겟 유저가 우리 서비스의 유저인지 체크
            if(userRepository.existsById(targetUser.getId())) {

                // 타겟 유저의 캐시가 존재 하는지 확인
                FollowRelation targetUserFollowRelation = getFollowRelationInCache(targetUser.getId());

                if (targetUserFollowRelation != null) {

                    targetUserFollowRelation.removeFollower(currentUserId);
                    followRelationRepository.update(targetUser.getId(), targetUserFollowRelation);
                }
            }
        }
        else throw new RuntimeException("깃허브 언 팔로우 중 에러 발생");  // 깃허브 API 명세서와 다른 응답이 오는거 같아서 조사중

    }

    private String getOAuthAccessToken(Long currentUserId){

        return oAuth2AuthorizedClientService.loadAuthorizedClient(GITHUB_REGISTRATION_ID, currentUserId.toString()).getAccessToken().getTokenValue();
    }

    private FollowRelation getFollowRelationInCache(Long userId){

        try {
            return followRelationRepository.findByUserId(userId).orElse(null);
        }
        catch (RedisConnectionFailureException ex) {
            log.warn("Redis Server Down");
            return null;
        }
    }

    // 개발용 임시 로그
    private void logFollowDetectInfo(Long userId, GithubFollowDetectInfo followDetectInfo) {

        log.info("{}의 팔로워, 팔로잉 페이지 정보 로그 (page size = {}) | followerMaxPage : {} , followingMaxPage : {}",
                userId,
                GITHUB_FOLLOW_QUERY_PAGING_SIZE,
                followDetectInfo.getMaxFollowerPage(),
                followDetectInfo.getMaxFollowingPage()
        );

        log.info("{}의 남은 요청 수 : ({}/5000)", userId, followDetectInfo.getRateLimitRemaining());
    }


}