package com.moyo.backend.follow.infrastructure.github;

import com.moyo.backend.follow.application.GithubFollowCommandClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GithubFollowCommandRestApiClient implements GithubFollowCommandClient {

    private final RestClient restClient;

    @Override
    public int follow(String username, String accessToken){
        return restClient.put()
                .uri("https://api.github.com/user/following/"+username)
                .headers(
                        header ->{
                            header.setBearerAuth(accessToken);
                            header.set(HttpHeaders.ACCEPT, "application/vnd.github+json");
                            header.set("X-GitHub-Api-Version", "2022-11-28");
                        }
                )
                .retrieve()
                .toBodilessEntity()
                .getStatusCode().value();
    }

    @Override
    public int unfollow(String username, String accessToken){
        return restClient.delete()
                .uri("https://api.github.com/user/following/"+username)
                .headers(
                        header ->{
                            header.setBearerAuth(accessToken);
                            header.set(HttpHeaders.ACCEPT, "application/vnd.github+json");
                            header.set("X-GitHub-Api-Version", "2022-11-28");
                        }
                )
                .retrieve()
                .toBodilessEntity()
                .getStatusCode().value();
    }

}
