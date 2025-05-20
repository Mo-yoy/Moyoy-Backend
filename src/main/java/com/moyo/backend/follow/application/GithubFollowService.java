package com.moyo.backend.follow.application;

import com.moyo.backend.follow.dto.*;
import com.moyo.backend.user.User;
import com.moyo.backend.user.UserRepository;
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
    private final GithubFollowClient githubFollowClient;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    public FollowDetectResponse detectFollowUserList(FollowDetectRequest request) {

        User user = userRepository.findById(request.getCurrentUserId()).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다: " + request.getCurrentUserId()));
        String oauthAccessToken = getOAuthAccessToken(request.getCurrentUserPrincipalName());
        UserFollowDetectMeta followDetectMeta = githubFollowClient.getUserFollowDetectMeta(oauthAccessToken, user.getUsername());
        logFollowDetectMeta(request.getCurrentUserId(), followDetectMeta);

        Set<GithubFollowUser> followingsSet = new LinkedHashSet<>();
        Set<GithubFollowUser> followersSet = new LinkedHashSet<>();

        for (int page=1; page <= followDetectMeta.getMaxFollowingPage(); page++)
            followingsSet.addAll(githubFollowClient.getFollowingList(oauthAccessToken, page));

        for (int page=1; page <= followDetectMeta.getMaxFollowerPage(); page++)
            followersSet.addAll(githubFollowClient.getFollowerList(oauthAccessToken, page));

        switch (request.getDetectType()) {
            case "mutual" -> followingsSet.retainAll(followersSet);
            case "following-only" -> followingsSet.removeAll(followersSet);
            case "follower-only" -> followersSet.removeAll(followingsSet);
            default -> throw new IllegalArgumentException("잘못된 FollowType: " + request.getDetectType());
        }

        List<GithubFollowUser> githubFollowUserList = new ArrayList<>();
        switch (request.getDetectType()) {
            case "mutual", "following-only" -> githubFollowUserList.addAll(followingsSet);
            case "follower-only" -> githubFollowUserList.addAll(followersSet);
        }

        List<GithubFollowUser> pagingList = new ArrayList<>();
        boolean lastPage = true;

        for(GithubFollowUser followUser : githubFollowUserList){

            if(request.getLastUserId() == 0 || followUser.getId() > request.getLastUserId()) pagingList.add(followUser);

            if(pagingList.size() == request.getPagingSize()+1){
                lastPage = false;
                pagingList.removeLast();
                break;
            }
        }


        return FollowDetectResponse.builder()
                .githubFollowUserList(pagingList)
                .lastPage(lastPage)
                .build();
    }

    public void follow(FollowCommandRequest request) {

        User user = userRepository.findById(request.getCurrentUserId()).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다: " + request.getCurrentUserId()));
        String oauthAccessToken = getOAuthAccessToken(request.getCurrentUserPrincipalName());

        UserFollowCommandMeta followCommandMeta = githubFollowClient.getUserFollowCommandMeta(oauthAccessToken, user.getUsername());
        log.info("{}의 남은 요청 수 : {}",request.getCurrentUserId(),followCommandMeta.getRateLimitRemaining());

        log.info("깃허브 팔로우 요청 | 요청자 ID : {} -> {}", request.getCurrentUserId(), request.getTargetUsername());

        if(githubFollowClient.follow(request.getTargetUsername(), oauthAccessToken) != 204)
            throw new RuntimeException("깃허브 팔로우 중 에러 발생");
    }

    public void unfollow(FollowCommandRequest request) {

        User user = userRepository.findById(request.getCurrentUserId()).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다: " + request.getCurrentUserId()));
        String oauthAccessToken = getOAuthAccessToken(request.getCurrentUserPrincipalName());

        UserFollowCommandMeta followCommandMeta = githubFollowClient.getUserFollowCommandMeta(oauthAccessToken, user.getUsername());
        log.info("{}의 남은 요청 수 : {}",request.getCurrentUserId(),followCommandMeta.getRateLimitRemaining());

        log.info("깃허브 언 팔로우 요청 | 요청자 ID : {} -> {}", request.getCurrentUserId(), request.getTargetUsername());

        if(githubFollowClient.unfollow(request.getTargetUsername(), oauthAccessToken) != 204)
            throw new RuntimeException("깃허브 언 팔로우 중 에러 발생");
    }

    private String getOAuthAccessToken(String userPrincipalName){

        return oAuth2AuthorizedClientService
                .loadAuthorizedClient(GITHUB_REGISTRATION_ID, userPrincipalName)
                .getAccessToken()
                .getTokenValue();
    }

    private void logFollowDetectMeta(Long userId, UserFollowDetectMeta followDetectMeta) {
        log.info("{} 팔로워, 팔로잉 페이지 정보 로그 (page size = {}) | followerMaxPage : {} , followingMaxPage : {}",
                userId,
                GITHUB_FOLLOW_QUERY_PAGING_SIZE,
                followDetectMeta.getMaxFollowerPage(),
                followDetectMeta.getMaxFollowingPage());

        log.info("{}의 남은 요청 수 : ({}/5000)", userId, followDetectMeta.getRateLimitRemaining());
    }

}