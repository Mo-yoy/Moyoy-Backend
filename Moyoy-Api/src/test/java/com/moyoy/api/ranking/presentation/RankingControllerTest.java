package com.moyoy.api.ranking.presentation;

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
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import com.moyoy.api.common.ApiControllerAdvice;
import com.moyoy.api.ranking.application.RankingService;
import com.moyoy.api.ranking.application.response.RankingSearchResult;

import com.moyoy.infra.database.mysql.query.dto.UserRankingView;

@WebMvcTest(value = RankingController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(ApiControllerAdvice.class)
class RankingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RankingService rankingService;

	@Test
	void 모든_개인유저_랭킹조회() throws Exception {

		// given
		UserRankingView userRankingView = new UserRankingView(1L, "image", "moyoy", 1234);
		List<UserRankingView> rankingUserViews = List.of(userRankingView);
		RankingSearchResult rankingSearchResult = new RankingSearchResult(rankingUserViews, true);

		Mockito.when(rankingService.searchAllUserRanking(any())).thenReturn(rankingSearchResult);

		mockMvc.perform(get("/api/v1/rankings")
			.param("period", "month")
			.param("page", "0")
			.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.rankingList").isArray())
			.andExpect(jsonPath("$.data.lastPage").value(true))

			// REST Docs
			.andDo(document("전체 개인 랭킹 조회 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("🏆 랭킹 조회")
					.summary("전체 개인 랭킹 조회 API")
					.description("우리 서비스의 전체 개인 랭킹을 조회합니다.")
					.queryParameters(
						parameterWithName("period")
							.description("조회할 랭킹 기간 <br/><br/> week <br/> month <br/> year")
							.type(SimpleType.STRING)
							.defaultValue("week"),
						parameterWithName("page")
							.description("조회할 페이지 (default: 0)")
							.type(SimpleType.INTEGER)
							.optional(),
						parameterWithName("size")
							.description("페이징 사이즈 (default: 20)")
							.type(SimpleType.INTEGER)
							.optional())
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						subsectionWithPath("data").description("응답 데이터"),
						subsectionWithPath("data.rankingList").description("👤 탐지된 사용자 목록"),
						fieldWithPath("data.rankingList[].userId").description("사용자 Id"),
						fieldWithPath("data.rankingList[].profileImageUrl").description("사용자 프로필 이미지 URL"),
						fieldWithPath("data.rankingList[].username").description("사용자 이름"),
						fieldWithPath("data.rankingList[].rankPoint").description("사용자 랭킹 점수"),
						fieldWithPath("data.lastPage").description("📌 마지막 페이지 여부"))
					.build())));

	}

}
