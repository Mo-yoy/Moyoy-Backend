package com.moyo.backend.githubFollow.infrastructure;

import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_FOLLOW_QUERY_PAGING_SIZE;

@Component
@RequiredArgsConstructor
public class GithubFollowApiRestClient implements GithubFollowApiClient {

    private final RestClient restClient;

    @Override
    public GithubFollowUser getFollowUserInfo(Long userId, String accessToken) {

        ResponseEntity<?> response = restClient.get()
                .uri("/user/{targetUserId}", userId)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toEntity(GithubFollowUser.class);

        return (GithubFollowUser) response.getBody();
    }

    @Override
    public GithubFollowDetectInfo fetchFollowDetectInfo(String username, String accessToken) {

        ResponseEntity<GithubFollowStatsResponse> response = restClient.get()
                .uri("/users/{username}", username)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toEntity(GithubFollowStatsResponse.class);

        int rateLimitRemaining = Integer.parseInt(response.getHeaders().get("X-RateLimit-Remaining").getFirst());
        GithubFollowStatsResponse githubFollowStatsResponse = response.getBody();
        githubFollowStatsResponse.setRateLimitRemaining(rateLimitRemaining);

        return GithubFollowDetectInfo.from(githubFollowStatsResponse);
    }


    // 깃허브 페이지는 1부터 시작
    @Override
    public List<GithubFollowUser> getFollowingList(int curPage, String accessToken){

        return restClient.get()
                .uri("/user/following?per_page=" + GITHUB_FOLLOW_QUERY_PAGING_SIZE +"&page=" + curPage)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<GithubFollowUser> getFollowerList(int curPage, String accessToken) {

        return restClient.get()
                .uri("/user/followers?per_page="+ GITHUB_FOLLOW_QUERY_PAGING_SIZE+ "&page=" + curPage)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public int follow(String targetUsername, String accessToken){
        return restClient.put()
                .uri("/user/following/{username}", targetUsername)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity()
                .getStatusCode().value();
    }

    @Override
    public int unfollow(String targetUsername, String accessToken) {
        return restClient.delete()
                .uri("/user/following/{username}", targetUsername)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity()
                .getStatusCode().value();
    }
}
