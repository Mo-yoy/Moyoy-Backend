package com.moyo.backend.domain.github_ranking.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.moyo.backend.common.exception.GlobalExceptionHandler;
import com.moyo.backend.domain.github_ranking.business.RankingSearchResult;
import com.moyo.backend.domain.github_ranking.business.RankingService;
import com.moyo.backend.domain.github_ranking.business.UserRankingDetails;

@WebMvcTest(value = RankingController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(GlobalExceptionHandler.class)
class RankingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RankingService rankingService;

	@Test
	void 모든_개인유저_랭킹조회() throws Exception {

		// given
		UserRankingDetails mockRankingDetails = new UserRankingDetails("img/url", "테스터1", 10000);
		Slice<UserRankingDetails> mockSlice = new SliceImpl<>(List.of(mockRankingDetails));

		RankingSearchResult mockResult = new RankingSearchResult(mockSlice);

		Mockito.when(rankingService.getAllUserRanking(anyString(), anyInt(), anyInt())).thenReturn(mockResult);

		mockMvc.perform(get("/api/v1/rankings")
			.param("duration", "monthly")
			.param("page", "1")
			.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userList").isArray())
			.andExpect(jsonPath("$.data.lastPage").value(true))

			// REST Docs
			.andDo(document("전체 개인 랭킹 조회 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("3. 랭킹 조회 🔢")
					.summary("전체 개인 랭킹 조회 API")
					.description("우리 서비스의 전체 개인 랭킹을 조회합니다.")
					.queryParameters(
						parameterWithName("duration").description("조회할 랭킹 기간 <br/><br/> week <br/> month <br/> year").type(SimpleType.STRING),
						parameterWithName("page").description("조회할 페이지 (default: 0)").type(SimpleType.INTEGER).optional(),
						parameterWithName("size").description("페이징 사이즈 (default: 20)").type(SimpleType.INTEGER).optional())
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						subsectionWithPath("data").description("응답 데이터"),
						subsectionWithPath("data.userList").description("👤 탐지된 사용자 목록"),
						fieldWithPath("data.userList[].profileImageUrl").description("사용자 프로필 이미지 URL"),
						fieldWithPath("data.userList[].username").description("사용자 이름"),
						fieldWithPath("data.userList[].rankPoint").description("사용자 랭킹 점수"),
						fieldWithPath("data.lastPage").description("📌 마지막 페이지 여부"))
					.build())));

	}

}
