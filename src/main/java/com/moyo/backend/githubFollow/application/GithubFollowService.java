package com.moyo.backend.githubFollow.application;

import com.moyo.backend.common.util.TimeSinceFormatter;
import com.moyo.backend.githubFollow.domain.GithubFollowRelation;
import com.moyo.backend.githubFollow.domain.GithubFollowRelationClient;
import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import com.moyo.backend.githubFollow.dto.GithubFollowDetectRequest;
import com.moyo.backend.githubFollow.dto.GithubFollowDetectResponse;
import com.moyo.backend.githubFollow.dto.GithubFollowUserResponseDto;
import com.moyo.backend.githubFollow.pagination.GithubFollowUserSliceService;
import com.moyo.backend.user.UserRepository;
import com.moyo.backend.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Slice;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

@Service
@RequiredArgsConstructor
public class GithubFollowService {

    private final UserRepository userRepository;
    private final GithubFollowRelationClient githubFollowRelationClient;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final GithubFollowUserSliceService githubFollowUserSliceService;

    public GithubFollowDetectResponse detectFollowUserList(Long userId, GithubFollowDetectRequest request) {

        String username = userRepository.findById(userId).orElseThrow(UserNotFoundException::new).getUsername();
        String oauthAccessToken = getOauthAccessToken(userId);
        GithubFollowRelation githubFollowRelation = githubFollowRelationClient.load(userId, username, oauthAccessToken);

        List<GithubFollowUser> allFollowUsers = githubFollowRelation.filterUsersByDetectType(request.getDetectType());

        Slice<GithubFollowUser> pagedSlice = githubFollowUserSliceService.getSlice(allFollowUsers, request.getLastFetchedUserId(), request.getPagingSize());

        List<GithubFollowUserResponseDto> usersResponseList = pagedSlice.getContent().stream()
                .map(GithubFollowUserResponseDto::from)
                .toList();

        return GithubFollowDetectResponse.builder()
                .userList(usersResponseList)
                .lastPage(pagedSlice.isLast())
                .totalUserCount(allFollowUsers.size())
                .lastSyncAt(TimeSinceFormatter.formatTimeSince(githubFollowRelation.getCreatedAt()))
                .build();
    }

    @CacheEvict(value = "followRelation", key = "#currentUserId")
    public void clearFollowCache(Long currentUserId) {
    }

    public void follow(Long currentUserId, Long targetUserId) {

        String oauthAccessToken = getOauthAccessToken(currentUserId);
        githubFollowRelationClient.follow(currentUserId, targetUserId, oauthAccessToken);
    }

    public void unfollow(Long currentUserId, Long targetUserId) {

        String oauthAccessToken = getOauthAccessToken(currentUserId);
        githubFollowRelationClient.unfollow(currentUserId, targetUserId, oauthAccessToken);
    }

    private String getOauthAccessToken(Long userId){
        return oAuth2AuthorizedClientService.loadAuthorizedClient(GITHUB_REGISTRATION_ID, userId.toString()).getAccessToken().getTokenValue();
    }
}
