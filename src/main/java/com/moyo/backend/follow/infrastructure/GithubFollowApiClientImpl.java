package com.moyo.backend.follow.infrastructure;

import com.moyo.backend.follow.application.GithubFollowApiClient;
import com.moyo.backend.follow.domain.GithubFollowDetectInfo;
import com.moyo.backend.follow.dto.UserFollowCommandMeta;
import com.moyo.backend.follow.dto.response.GithubFollowUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;

@Component
@RequiredArgsConstructor
public class GithubFollowApiClientImpl implements GithubFollowApiClient {

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
    public UserFollowCommandMeta getUserFollowCommandMeta(String accessToken, String username) {

        ResponseEntity<?> response = restClient.get()
                .uri("/users/{username}", username)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();

        int rateLimitRemaining = Integer.parseInt(response.getHeaders().get("X-RateLimit-Remaining").getFirst());

        return new UserFollowCommandMeta(rateLimitRemaining);
    }

    // 깃허브 페이지는 1부터 시작
    @Override
    public List<GithubFollowUserInfoResponse> getFollowingList(String accessToken, int curPage){

        return restClient.get()
                .uri("/user/following?per_page=" + GITHUB_FOLLOW_QUERY_PAGING_SIZE +"&page=" + curPage)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<GithubFollowUserInfoResponse> getFollowerList(String accessToken, int curPage) {

        return restClient.get()
                .uri("/user/followers?per_page="+ GITHUB_FOLLOW_QUERY_PAGING_SIZE+ "&page=" + curPage)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public GithubFollowDetectInfo fetchFollowDetectInfo(String currentUsername, String oauthAccessToken) {

        ResponseEntity<GithubFollowStatsResponse> response = restClient.get()
                .uri("/users/{username}", currentUsername)
                .headers(header -> header.setBearerAuth(oauthAccessToken))
                .retrieve()
                .toEntity(GithubFollowStatsResponse.class);

        int rateLimitRemaining = Integer.parseInt(response.getHeaders().get("X-RateLimit-Remaining").getFirst());
        GithubFollowStatsResponse githubFollowStatsResponse = response.getBody();
        githubFollowStatsResponse.setRateLimitRemaining(rateLimitRemaining);

        return GithubFollowDetectInfo.from(githubFollowStatsResponse);
    }


}
