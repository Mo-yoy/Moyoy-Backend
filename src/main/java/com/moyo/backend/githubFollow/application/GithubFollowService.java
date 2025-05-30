package com.moyo.backend.githubFollow.application;

import com.moyo.backend.githubFollow.domain.GithubFollowRelation;
import com.moyo.backend.githubFollow.domain.GithubFollowRelationClient;
import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import com.moyo.backend.githubFollow.dto.GithubFollowDetectRequest;
import com.moyo.backend.githubFollow.dto.GithubFollowDetectResponse;
import com.moyo.backend.githubFollow.infrastructure.GithubFollowCacheManager;
import com.moyo.backend.security.oauth.GithubOAuthTokenProvider;
import com.moyo.backend.user.UserRepository;
import com.moyo.backend.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubFollowService {

    private final UserRepository userRepository;
    private final GithubFollowRelationClient githubFollowRelationClient;
    private final GithubFollowUserSliceService githubFollowUserSliceService;
    private final GithubFollowCacheManager githubFollowCacheManager;
    private final GithubOAuthTokenProvider githubOAuthTokenProvider;

    public GithubFollowDetectResponse detectFollowUserList(Long userId, GithubFollowDetectRequest request) {

        String username = userRepository.findById(userId).orElseThrow(UserNotFoundException::new).getUsername();
        String oauthAccessToken = githubOAuthTokenProvider.getGithubAccessToken(userId);
        GithubFollowRelation githubFollowRelation = githubFollowRelationClient.load(userId, username, oauthAccessToken);

        List<GithubFollowUser> filteredUserList = githubFollowRelation.filterUsersByDetectType(request.getDetectType());

        Slice<GithubFollowUser> pagedSlice = githubFollowUserSliceService.getSlice(filteredUserList, request.getLastFetchedUserId(), request.getPagingSize());

        return GithubFollowDetectResponse.of(pagedSlice, githubFollowRelation.getCreatedAt(), filteredUserList.size());
    }

    public void clearFollowCache(Long currentUserId) {
        githubFollowCacheManager.evictCache(currentUserId);
    }

    public void follow(Long currentUserId, Long targetUserId) {

        String oauthAccessToken = githubOAuthTokenProvider.getGithubAccessToken(currentUserId);
        githubFollowRelationClient.follow(currentUserId, targetUserId, oauthAccessToken);
    }

    public void unfollow(Long currentUserId, Long targetUserId) {

        String oauthAccessToken = githubOAuthTokenProvider.getGithubAccessToken(currentUserId);
        githubFollowRelationClient.unfollow(currentUserId, targetUserId, oauthAccessToken);
    }


}
