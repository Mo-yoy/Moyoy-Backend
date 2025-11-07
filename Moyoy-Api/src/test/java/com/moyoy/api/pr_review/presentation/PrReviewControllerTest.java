package com.moyoy.api.pr_review.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import com.moyoy.api.pr_review.application.PrReviewService;
import com.moyoy.api.pr_review.application.PrReviewSummary;
import com.moyoy.api.pr_review.application.response.PrReviewListResult;

import com.moyoy.common.annotation.ControllerTest;

@ControllerTest(controllers = PrReviewController.class)
class PrReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PrReviewService prReviewService;

	@Test
	@DisplayName("PR 리뷰 요청글 전체 조회 성공 - 200 OK")
	void getPrReviewList_success() throws Exception {

		// given
		PrReviewSummary summary1 = new PrReviewSummary(
			"https://avatars.githubusercontent.com/u/93702146?v=4",
			"zzaekkii",
			"BACKEND",
			"Refactor: 아키텍처 변경 + 도메인 모델과 JPA 엔티티 분리",
			52,
			LocalDateTime.of(2025, 11, 5, 17, 30, 0));
		PrReviewSummary summary2 = new PrReviewSummary(
			"https://avatars.githubusercontent.com/u/100277876?s=80&v=4",
			"seungryul99",
			"BACKEND",
			"fix: Add missing TTL when updating cache",
			120,
			LocalDateTime.of(2025, 10, 22, 21, 55, 0));
		PrReviewSummary summary3 = new PrReviewSummary(
			"https://avatars.githubusercontent.com/u/60962533?s=80&v=4",
			"jungwoo3490",
			"FRONTEND",
			"fix: Video asset 경로 이슈 수정",
			187,
			LocalDateTime.of(2025, 10, 22, 21, 55, 0));

		List<PrReviewSummary> summaries = List.of(summary1, summary2, summary3);
		PrReviewListResult result = new PrReviewListResult(summaries, true);

		given(prReviewService.getPrReviewList(any())).willReturn(result);

		// when
		mockMvc.perform(get("/api/v1/pr-reviews")
			.param("status", "")
			.param("order", "")
			.param("position", "")
			.param("lastReviewId", "")
			.param("size", ""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.prReviews").isArray())
			.andExpect(jsonPath("$.data.isLast").value(true))
			.andDo(document("PR 리뷰 요청글 전체 조회 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("💬 PR 리뷰 요청")
					.summary("PR 리뷰 요청글 전체 조회 API")
					.description("""
						게시된 PR 리뷰 요청글 전체를 조회합니다.<br/>
						게시글 상태(open/closed), 정렬 기준(최신순, 조회수순), 직무태그 필터링이 가능합니다.
						""")
					.queryParameters(
						parameterWithName("status").description("PR 상태 (open 또는 closed)").type(SimpleType.STRING).optional(),
						parameterWithName("order").description("정렬 기준 (예: createdAt-desc, hitCount-desc)").type(SimpleType.STRING).optional(),
						parameterWithName("position").description("직군 필터 (예: BACKEND, FRONTEND)").type(SimpleType.STRING).optional(),
						parameterWithName("lastReviewId").description("이전 페이지의 마지막 요청글 ID (기본값: 0)").type(SimpleType.INTEGER).optional(),
						parameterWithName("size").description("페이지 크기 (1~100, 기본값: 20)").type(SimpleType.INTEGER).optional())
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						subsectionWithPath("data.prReviews").description("PR 리뷰 요청글 전체 목록"),
						fieldWithPath("data.prReviews[].profileImageUrl").description("작성자 프로필 이미지 URL"),
						fieldWithPath("data.prReviews[].username").description("작성자 이름"),
						fieldWithPath("data.prReviews[].position").description("요청글 직무 태그"),
						fieldWithPath("data.prReviews[].title").description("요청글 제목"),
						fieldWithPath("data.prReviews[].hitCount").description("조회수"),
						fieldWithPath("data.prReviews[].createdAt").description("작성일자"),
						fieldWithPath("data.isLast").description("마지막 페이지 여부"))
					.build())));
	}
}
