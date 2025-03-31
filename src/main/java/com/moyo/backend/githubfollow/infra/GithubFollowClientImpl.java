package com.moyo.backend.githubfollow.infra;

import com.moyo.backend.githubfollow.dto.GithubFollowUser;
import com.moyo.backend.githubfollow.dto.UserFollowCommandMeta;
import com.moyo.backend.githubfollow.dto.UserFollowDetectMeta;
import com.moyo.backend.githubfollow.dto.UserFollowStats;
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
    public List<GithubFollowUser> getFollowingList(String accessToken, int curPage){

        return restClient.get()
                .uri("/user/following?per_page=" + GITHUB_FOLLOW_QUERY_PAGING_SIZE +"&page=" + curPage)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<GithubFollowUser> getFollowerList(String accessToken, int curPage) {

        return restClient.get()
                .uri("/user/followers?per_page="+ GITHUB_FOLLOW_QUERY_PAGING_SIZE+ "&page=" + curPage)
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
