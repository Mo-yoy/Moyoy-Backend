package com.moyo.backend.domain.github_ranking.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.moyo.common.constant.TestConstant.*;
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

import com.moyo.backend.common.exception.handler.GlobalExceptionHandler;
import com.moyo.backend.domain.github_ranking.business.RankingSearchResult;
import com.moyo.backend.domain.github_ranking.business.RankingService;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingWithUser;
import com.moyo.backend.domain.user.implement.Role;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.common.annotation.WithMockMoyoyUser;

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
		User mockUser = new User(1L, 1, "테스터1", "img/url", Role.USER);
		Ranking mockRanking = new Ranking(1L, 1L, "A", 100, 1000, 10000);
		RankingWithUser mockRankingWithUser = new RankingWithUser(mockRanking, mockUser);

		List<RankingWithUser> rankingWithUsers = List.of(mockRankingWithUser);
		RankingSearchResult rankingSearchResult = new RankingSearchResult(rankingWithUsers, true);

		Mockito.when(rankingService.searchAllUserRanking(any()))
			.thenReturn(rankingSearchResult);

		mockMvc.perform(get("/api/v1/rankings")
			.param("period", "month")
			.param("page", "0")
			.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userList").isArray())
			.andExpect(jsonPath("$.data.lastPage").value(true))

			// REST Docs
			.andDo(document("전체 개인 랭킹 조회 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("🏆 랭킹 조회")
					.summary("전체 개인 랭킹 조회 API")
					.description("우리 서비스의 전체 개인 랭킹을 조회합니다.")
					.queryParameters(
						parameterWithName("period").description("조회할 랭킹 기간 <br/><br/> week <br/> month <br/> year").type(SimpleType.STRING),
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

	@Test
	@WithMockMoyoyUser
	void 사용자의_팔로잉_유저중_우리_서비스회원_랭킹조회() throws Exception {

		// given
		User mockUser = new User(1L, 1, "테스터1", "img/url", Role.USER);
		Ranking mockRanking = new Ranking(1L, 1L, "A", 100, 1000, 10000);
		RankingWithUser mockRankingWithUser = new RankingWithUser(mockRanking, mockUser);

		List<RankingWithUser> rankingWithUsers = List.of(mockRankingWithUser);
		RankingSearchResult rankingSearchResult = new RankingSearchResult(rankingWithUsers, true);

		Mockito.when(rankingService.searchUserFollowingsRanking(anyLong(), any()))
			.thenReturn(rankingSearchResult);

		mockMvc.perform(get("/api/v1/users/me/followings/rankings")
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
			.param("period", "month")
			.param("page", "0")
			.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userList").isArray())
			.andExpect(jsonPath("$.data.lastPage").value(true))

			// REST Docs
			.andDo(document("팔로잉 랭킹 조회 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("🏆 랭킹 조회")
					.summary("사용자의 팔로잉 유저들 중에서 우리 서비스 회원들 랭킹 조회 API")
					.description("사용자가 깃허브 상에서 팔로우 중인 유저들 중, 우리 서비스의 회원인 사용자의 랭킹 리스트를 반환합니다.")
					.queryParameters(
						parameterWithName("period").description("조회할 랭킹 기간 <br/><br/> week <br/> month <br/> year").type(SimpleType.STRING),
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
