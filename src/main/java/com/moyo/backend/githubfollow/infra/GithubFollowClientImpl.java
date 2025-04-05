package com.moyo.backend.githubfollow.infra;

import com.moyo.backend.githubfollow.dto.GithubFollowUserResponse;
import com.moyo.backend.githubfollow.dto.UserFollowCommandMeta;
import com.moyo.backend.githubfollow.dto.UserFollowDetectMeta;
import com.moyo.backend.githubfollow.dto.UserFollowStats;
import com.moyo.backend.githubfollow.model.FollowUser;
import com.moyo.backend.githubfollow.service.GithubFollowClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;

@Component
@RequiredArgsConstructor
public class GithubFollowClientImpl implements GithubFollowClient {

    private final RestClient restClient;

    @Override
    public int follow(String username, String accessToken){
        return restClient.put()
                .uri("/user/following/{username}", username)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity()
                .getStatusCode().value();
    }

    @Override
    public int unfollow(String username, String accessToken){
        return restClient.delete()
                .uri("/user/following/{username}", username)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity()
                .getStatusCode().value();
    }

    @Override
    public UserFollowDetectMeta getUserFollowDetectMeta(String accessToken, String username) {

        ResponseEntity<?> response = restClient.get()
                .uri("/users/{username}", username)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toEntity(UserFollowStats.class);

        UserFollowStats userFollowStats = (UserFollowStats) response.getBody();
        int rateLimitRemaining = Integer.parseInt(response.getHeaders().get("X-RateLimit-Remaining").getFirst());

        return UserFollowDetectMeta.builder()
                .followerCnt(userFollowStats.getFollowerCnt())
                .followingCnt(userFollowStats.getFollowingCnt())
                .rateLimitRemaining(rateLimitRemaining)
                .build();
    }

    @Override
    public UserFollowCommandMeta getUserFollowCommandMeta(String accessToken, String targetUsername) {

        ResponseEntity<?> response = restClient.get()
                .uri("/users/{username}", targetUsername)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toEntity(GithubFollowUserResponse.class);

        int rateLimitRemaining = Integer.parseInt(response.getHeaders().get("X-RateLimit-Remaining").getFirst());

        GithubFollowUserResponse userResponse = (GithubFollowUserResponse) response.getBody();

        return UserFollowCommandMeta.builder()
                .rateLimitRemaining(rateLimitRemaining)
                .targetUserId(userResponse.getId())
                .targetUsername(userResponse.getUsername())
                .targetUserProfileImgUrl(userResponse.getProfileImgUrl())
                .build();
    }

    // 깃허브 페이지는 1부터 시작
    @Override
    public List<FollowUser> getFollowingList(String accessToken, int curPage){

        List<GithubFollowUserResponse> userResponses = restClient.get()
                .uri("/user/following?per_page=" + GITHUB_FOLLOW_QUERY_PAGING_SIZE +"&page=" + curPage)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return userResponses.stream()
                .map(userResponse -> new FollowUser(userResponse.getId(), userResponse.getUsername(), userResponse.getProfileImgUrl()))
                .toList();
    }

    @Override
    public List<FollowUser> getFollowerList(String accessToken, int curPage) {

        List<GithubFollowUserResponse> userResponses = restClient.get()
                .uri("/user/followers?per_page="+ GITHUB_FOLLOW_QUERY_PAGING_SIZE+ "&page=" + curPage)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return userResponses.stream()
                .map(userResponse -> new FollowUser(userResponse.getId(), userResponse.getUsername(), userResponse.getProfileImgUrl()))
                .toList();
    }
}
