package com.moyo.backend.follow.presentation;

import com.moyo.backend.common.exception.GlobalExceptionHandler;
import com.moyo.backend.follow.application.GithubFollowService;
import com.moyo.backend.follow.dto.response.GithubFollowDetectResponse;
import com.moyo.backend.security.oauth.GithubOAuth2User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(value = GithubFollowController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(GlobalExceptionHandler.class)
class GithubFollowControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GithubFollowService githubFollowService;

    // 테스트 클래스 멤버 변수로 선언
    @Captor
    ArgumentCaptor<Long> currentUserIdCaptor;

    @Test
    void testGetFollowUserList_success() throws Exception {

        // given
        GithubFollowDetectResponse mockResponse = GithubFollowDetectResponse.builder()
                .userList(List.of())
                .totalUserCount(0)
                .lastPage(true)
                .build();

        given(githubFollowService.detectFollowUserList(anyLong(), any())).willReturn(mockResponse);

        // 현재 사용자 ID를 담은 Authentication 객체를 직접 만들어서
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new GithubOAuth2User(Collections.EMPTY_SET, Map.of("id", 1L)), null, Collections.emptyList()
        );
        // when
        mockMvc.perform(get("/users/me/followings/{detectType}", "mutual")
                        .header("Authorization", "Bearer testToken...")
                        .param("lastUserId", "22")
                        .param("pageSize", "1")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());

        // then - 서비스 호출 시 첫 번째 파라미터(currentUserId)를 캡처해서 검증
        verify(githubFollowService).detectFollowUserList(currentUserIdCaptor.capture(), any());
        Long capturedCurrentUserId = currentUserIdCaptor.getValue();

        assertEquals(1L, capturedCurrentUserId);

    }



}