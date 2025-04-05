package com.moyo.backend.githubfollow.service;

import com.moyo.backend.githubfollow.dto.*;
import com.moyo.backend.githubfollow.model.FollowUser;
import com.moyo.backend.user.User;
import com.moyo.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;
import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubFollowService {

    private final UserRepository userRepository;
    private final GithubFollowClient githubFollowClient;
    private final GithubFollowRepository githubFollowRepository;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public FollowDetectResponse getFollowUserList(FollowDetectRequest request){

        User user = userRepository.findById(request.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다: " + request.getCurrentUserId()));

        List<FollowUser> followings = new ArrayList<>();
        List<FollowUser> followers = new ArrayList<>();

        Set<FollowUser> followingsSet = new LinkedHashSet<>();
        Set<FollowUser> followersSet = new LinkedHashSet<>();

        // Redis Lazy Loading
        if(!githubFollowRepository.existByUserId(request.getCurrentUserId())){

            log.info("캐시에 {}의 팔로잉 관련 데이터가 존재 하지 않음.", request.getCurrentUserId());

            String oauthAccessToken = getOAuthAccessToken(request.getCurrentUserPrincipalName());
            UserFollowDetectMeta followDetectMeta = githubFollowClient.getUserFollowDetectMeta(oauthAccessToken, user.getUsername());
            logFollowDetectMeta(request.getCurrentUserId(), followDetectMeta);

            for (int page=1; page <= followDetectMeta.getMaxFollowingPage(); page++)
                followings.addAll(githubFollowClient.getFollowingList(oauthAccessToken, page));

            for (int page=1; page <= followDetectMeta.getMaxFollowerPage(); page++)
                followers.addAll(githubFollowClient.getFollowerList(oauthAccessToken, page));

            githubFollowRepository.saveFollowingList(request.getCurrentUserId(), followings);
            githubFollowRepository.saveFollowerList(request.getCurrentUserId(), followers);
            githubFollowRepository.saveFollowCreatedAt(request.getCurrentUserId(), LocalDateTime.now());

            followingsSet.addAll(followings);
            followersSet.addAll(followers);
        }
        else {
            followingsSet.addAll(githubFollowRepository.getFollowingList(request.getCurrentUserId()));
            followersSet.addAll(githubFollowRepository.getFollowerList(request.getCurrentUserId()));
        }

        switch (request.getDetectType()) {
            case "mutual" -> followingsSet.retainAll(followersSet);
            case "following-only" -> followingsSet.removeAll(followersSet);
            case "follower-only" -> followersSet.removeAll(followingsSet);
            default -> throw new IllegalArgumentException("잘못된 FollowType: " + request.getDetectType());
        }

        List<FollowUser> followUserList = new ArrayList<>();
        switch (request.getDetectType()) {
            case "mutual", "following-only" -> followUserList.addAll(followingsSet);
            case "follower-only" -> followUserList.addAll(followersSet);
        }


        List<FollowUser> pagingList = new ArrayList<>();
        boolean lastPage = true;

        for(FollowUser followUser : followUserList){

            if(request.getLastUserId() == 0 || followUser.getId() > request.getLastUserId()) pagingList.add(followUser);

            if(pagingList.size() == request.getPagingSize()+1){
                lastPage = false;
                pagingList.removeLast();
                break;
            }
        }

        return FollowDetectResponse.builder()
                .userList(pagingList)
                .lastPage(lastPage)
                .build();
    }

    public void follow(FollowCommandRequest request) {

        String oauthAccessToken = getOAuthAccessToken(request.getCurrentUserPrincipalName());
        UserFollowCommandMeta followCommandMeta = githubFollowClient.getUserFollowCommandMeta(oauthAccessToken, request.getTargetUsername());
        FollowUser followUser = new FollowUser(request.getTargetUserId(), request.getTargetUsername(), followCommandMeta.getTargetUserProfileImgUrl());

        if (followCommandMeta.isInvalidRequest(request.getTargetUserId())){
            log.warn("팔로우 하려는 사람이 닉네임을 변경했고 해당 이름을 다른 사용자가 사용하는 중임");

            throw new RuntimeException("팔로우 하려는 사람이 닉네임을 변경했고 해당 이름을 다른 사용자가 사용하는 중임");
        }

        // 팔로우 하려는 사람의 닉네임이 변경됨
        if (followCommandMeta.isTargetUserNameChanged(request.getTargetUserId(), request.getTargetUsername())) {
            log.info("팔로우 하려는 사람의 닉네임이 변경됨 {} -> {}", request.getTargetUsername(), followCommandMeta.getTargetUsername());
            followUser.changeUsername(followCommandMeta.getTargetUsername());
        }

        log.info("{}의 남은 요청 수 : {}", request.getCurrentUserId(), followCommandMeta.getRateLimitRemaining());
        log.info("깃허브 팔로우 요청 | 요청자 ID : {} -> {}", request.getCurrentUserId(), request.getTargetUsername());

        if (githubFollowClient.follow(request.getTargetUsername(), oauthAccessToken) != 204)
            throw new RuntimeException("깃허브 팔로우 중 에러 발생");

        // write through
        // 내가 상대를 팔로우 캐시에 추가, 사용자가 화면을 띄워둔채로 캐시 만료 후 요청 날릴걸 대비
        if (githubFollowRepository.existByUserId(request.getCurrentUserId())){

            githubFollowRepository.saveFollowing(request.getCurrentUserId(), followUser);

            User user = userRepository.findById(request.getCurrentUserId()).get();

            // 상대의 팔로우 캐시가 존재 하고 우리 서비스의 유저 라면 상대의 팔로우 캐시에도 추가
            if(githubFollowRepository.existByUserId(request.getTargetUserId())){

                FollowUser currentUser = new FollowUser(user.getId(), user.getUsername(), user.getProfileImgUrl());
                githubFollowRepository.saveFollower(request.getTargetUserId(), currentUser);
            }
        }

    }

    public void unfollow(FollowCommandRequest request) {

        String oauthAccessToken = getOAuthAccessToken(request.getCurrentUserPrincipalName());

        UserFollowCommandMeta followCommandMeta = githubFollowClient.getUserFollowCommandMeta(oauthAccessToken, request.getTargetUsername());
        log.info("{}의 남은 요청 수 : {}",request.getCurrentUserId(),followCommandMeta.getRateLimitRemaining());

        FollowUser followUser = new FollowUser(request.getTargetUserId(), request.getTargetUsername(), followCommandMeta.getTargetUserProfileImgUrl());

        if (followCommandMeta.isInvalidRequest(request.getTargetUserId())){
            log.warn("언 팔로우 하려는 사람이 닉네임을 변경 했고 해당 이름을 다른 사용자가 사용하는 중임");

            throw new RuntimeException("언 팔로우 하려는 사람이 닉네임을 변경 했고 해당 이름을 다른 사용자가 사용하는 중임");
        }

        // 언 팔로우 하려는 사람의 닉네임이 변경됨
        if (followCommandMeta.isTargetUserNameChanged(request.getTargetUserId(), request.getTargetUsername())) {
            log.info("언 팔로우 하려는 사람의 닉네임이 변경됨 {} -> {}", request.getTargetUsername(), followCommandMeta.getTargetUsername());
            followUser.changeUsername(followCommandMeta.getTargetUsername());
        }

        log.info("깃허브 언 팔로우 요청 | 요청자 ID : {} -> {}", request.getCurrentUserId(), request.getTargetUsername());

        if(githubFollowClient.unfollow(followUser.getUsername(), oauthAccessToken) != 204)
            throw new RuntimeException("깃허브 언 팔로우 중 에러 발생");

        // 내 팔로잉 캐시에서 해당유저 제거
        if(githubFollowRepository.existByUserId(request.getCurrentUserId())){

            githubFollowRepository.deleteFollowingUser(request.getCurrentUserId(), request.getTargetUserId());

            // 상대의 팔로워에서 나 제거
            if(githubFollowRepository.existByUserId(request.getTargetUserId())){

                githubFollowRepository.deleteFollowerUser(request.getTargetUserId(), request.getCurrentUserId());
            }
        }

    }


    public void deleteCache(Long userId) {

        githubFollowRepository.deleteFollowCache(userId);
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