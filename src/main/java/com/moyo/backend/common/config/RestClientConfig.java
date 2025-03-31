package com.moyo.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(){
        return RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .defaultHeader(ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }
}