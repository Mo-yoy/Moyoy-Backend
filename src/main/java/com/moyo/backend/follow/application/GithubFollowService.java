package com.moyo.backend.follow.application;

import com.moyo.backend.common.constant.MoyoConstants;
import com.moyo.backend.follow.domain.FollowRelation;
import com.moyo.backend.follow.domain.GithubFollowDetectInfo;
import com.moyo.backend.follow.dto.*;
import com.moyo.backend.follow.dto.request.GithubFollowDetectRequest;
import com.moyo.backend.follow.dto.response.FollowDetectResponse;
import com.moyo.backend.follow.dto.response.GithubFollowUserInfoResponse;
import com.moyo.backend.follow.exception.GithubRateLimitRemainingExceedException;
import com.moyo.backend.user.User;
import com.moyo.backend.user.UserRepository;
import com.moyo.backend.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;
import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubFollowService {

    private final UserRepository userRepository;
    private final GithubFollowApiClient githubFollowApiClient;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public FollowDetectResponse detectFollowUserList(Long currentUserId, GithubFollowDetectRequest request) {

        FollowRelation followRelation = null;

        // 캐시 (Redis) 에서 FollowRelation 도메인 모델 조회
        



        // 캐시 미스

        // 깃허브에 현재 사용자가 요청을 보낼 수 있는지 예비 요청을 보냄 (username, oauthAccessToken 필요)
        String currentUsername = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new).getUsername();
        String oauthAccessToken = oAuth2AuthorizedClientService.loadAuthorizedClient(GITHUB_REGISTRATION_ID, currentUserId.toString()).getAccessToken().getTokenValue();
        GithubFollowDetectInfo followDetectInfo = githubFollowApiClient.fetchFollowDetectInfo(currentUsername, oauthAccessToken);
        logFollowDetectInfo(currentUserId, followDetectInfo);

        // Github API 에서 FollowRelation 도메인 모델 조회
        if(followDetectInfo.canFollowFetchRequest()){

            List<GithubFollowUserInfoResponse> followers = new ArrayList<>();
            List<GithubFollowUserInfoResponse> followings = new ArrayList<>();

            for (int currentPage = 1; currentPage <= followDetectInfo.getMaxFollowerPage(); currentPage++){

                followers.addAll(githubFollowApiClient.getFollowerList(oauthAccessToken, currentPage));
            }

            for (int currentPage = 1; currentPage <= followDetectInfo.getMaxFollowingPage(); currentPage++){

                followings.addAll(githubFollowApiClient.getFollowingList(oauthAccessToken, currentPage));
            }

            followRelation = FollowRelation.builder()
                    .userId(currentUserId)
                    .githubFollowers(followers)
                    .githubFollowings(followings)
                    .build();

        }
        else throw new GithubRateLimitRemainingExceedException();

        
        // FollowRelation 도메인 객체의 비즈니스 로직 수행
        List<GithubFollowUserInfoResponse> followUserList = followRelation.filterUsersByDetectType(request.getDetectType());

        // LastFetchedUserId를 기반 으로 Slice 처리 후 반환
        long lastFetchedId = request.getLastFetchedUserId();
        int pageSize = request.getPagingSize();

        List<GithubFollowUserInfoResponse> pagingList = new ArrayList<>(
                followUserList.stream()
                        .filter(user -> lastFetchedId == 0 || user.getId() > lastFetchedId)
                        .limit(pageSize + 1)
                        .toList()   // 불변 리스트
        );

        boolean lastPage = pagingList.size() <= pageSize;
        if (!lastPage) pagingList.removeLast();

        return FollowDetectResponse.builder()
                .userList(pagingList)
                .lastPage(lastPage)
                .totalUserCount(followUserList.size())
                .build();
    }

    public void follow(FollowCommandRequest request) {

        User user = userRepository.findById(request.getCurrentUserId()).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다: " + request.getCurrentUserId()));
        String oauthAccessToken = getOAuthAccessToken(request.getCurrentUserPrincipalName());

        UserFollowCommandMeta followCommandMeta = githubFollowApiClient.getUserFollowCommandMeta(oauthAccessToken, user.getUsername());
        log.info("{}의 남은 요청 수 : {}",request.getCurrentUserId(),followCommandMeta.getRateLimitRemaining());

        log.info("깃허브 팔로우 요청 | 요청자 ID : {} -> {}", request.getCurrentUserId(), request.getTargetUsername());

        if(githubFollowApiClient.follow(request.getTargetUsername(), oauthAccessToken) != 204)
            throw new RuntimeException("깃허브 팔로우 중 에러 발생");
    }

    public void unfollow(FollowCommandRequest request) {

        User user = userRepository.findById(request.getCurrentUserId()).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다: " + request.getCurrentUserId()));
        String oauthAccessToken = getOAuthAccessToken(request.getCurrentUserPrincipalName());

        UserFollowCommandMeta followCommandMeta = githubFollowApiClient.getUserFollowCommandMeta(oauthAccessToken, user.getUsername());
        log.info("{}의 남은 요청 수 : {}",request.getCurrentUserId(),followCommandMeta.getRateLimitRemaining());

        log.info("깃허브 언 팔로우 요청 | 요청자 ID : {} -> {}", request.getCurrentUserId(), request.getTargetUsername());

        if(githubFollowApiClient.unfollow(request.getTargetUsername(), oauthAccessToken) != 204)
            throw new RuntimeException("깃허브 언 팔로우 중 에러 발생");
    }

    private String getOAuthAccessToken(String userPrincipalName){

        return oAuth2AuthorizedClientService
                .loadAuthorizedClient(GITHUB_REGISTRATION_ID, userPrincipalName)
                .getAccessToken()
                .getTokenValue();
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