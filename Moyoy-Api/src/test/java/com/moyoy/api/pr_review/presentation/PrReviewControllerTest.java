package com.moyoy.api.pr_review.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.moyoy.common.constant.TestConstant.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import com.moyoy.api.pr_review.application.PrReviewService;
import com.moyoy.api.pr_review.application.PrReviewSummary;
import com.moyoy.api.pr_review.application.response.PrReviewCreateResult;
import com.moyoy.api.pr_review.application.response.PrReviewDetailResult;
import com.moyoy.api.pr_review.application.response.PrReviewListResult;
import com.moyoy.api.pr_review.application.response.PrReviewUpdateResult;

import com.moyoy.domain.pr_review.Status;
import com.moyoy.domain.pr_review.error.PrReviewErrorCode;

import com.moyoy.common.annotation.ControllerTest;
import com.moyoy.common.annotation.WithMockMoyoyUser;
import com.moyoy.common.error.CommonErrorCode;

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

	@Nested
	@DisplayName("PR 리뷰 요청글 조회")
	class PrReviewValidationTest {

		@Test
		@DisplayName("잘못된 요청글 상태(status) - 400 BAD REQUEST")
		void invalidStatus_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/pr-reviews")
				.param("status", "invalid")
				.param("order", "")
				.param("position", "")
				.param("lastReviewId", "0")
				.param("size", "20"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(PrReviewErrorCode.INVALID_STATUS.getCode()));
		}

		@Test
		@DisplayName("파라미터 입력 값이 null - 각각 기본값 적용")
		void nullParams_defaultsApplied() throws Exception {
			// given
			given(prReviewService.getPrReviewList(any()))
				.willReturn(new PrReviewListResult(List.of(), true));

			// when & then
			mockMvc.perform(get("/api/v1/pr-reviews"))
				.andExpect(status().isOk());

			verify(prReviewService).getPrReviewList(
				argThat(req -> req.status().equals(Status.OPEN) &&
					req.order().equals("createdAt-desc") &&
					req.position() == null &&
					req.lastReviewId() == 0 &&
					req.size() == 20));
		}

		@Test
		@DisplayName("잘못된 정렬 기준 - 400 BAD REQUEST")
		void invalidOrder_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/pr-reviews")
				.param("order", "invalid-order"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(PrReviewErrorCode.INVALID_ORDER.getCode()));
		}

		@Test
		@DisplayName("잘못된 직무 태그(position) - 400 BAD REQUEST")
		void invalidPosition_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/pr-reviews")
				.param("position", "프론트엔도"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(PrReviewErrorCode.INVALID_POSITION.getCode()));
		}

		@Test
		@DisplayName("한글 직무 태그(position) - 영어로 자동 변환")
		void koreanPosition_englishTranslated() throws Exception {
			// given
			given(prReviewService.getPrReviewList(any()))
				.willReturn(new PrReviewListResult(List.of(), true));

			// when & then
			mockMvc.perform(get("/api/v1/pr-reviews")
				.param("position", "프론트엔드"))
				.andExpect(status().isOk());
		}

		@Test
		@DisplayName("lastReviewId가 음수 - 400 BAD REQUEST")
		void lastReviewIdNegative_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/pr-reviews")
				.param("lastReviewId", "-1"))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("size가 0 이하 - 400 BAD REQUEST")
		void sizeTooSmall_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/pr-reviews")
				.param("size", "0"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_PARAM.getCode()));
		}

		@Test
		@DisplayName("size가 100 초과 - 400 BAD REQUEST")
		void sizeTooLarge_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/pr-reviews")
				.param("status", "open")
				.param("order", "createdAt-desc")
				.param("position", "BACKEND")
				.param("lastReviewId", "0")
				.param("size", "10000000000001"))
				.andExpect(status().isBadRequest());
		}
	}

	@Test
	@WithMockMoyoyUser(id = 93702146L)
	@DisplayName("내 PR 리뷰 요청글 전체 조회 성공 - 200 OK")
	void getMyPrReviewList_success() throws Exception {
		// given
		PrReviewSummary summary1 = new PrReviewSummary(
			"https://avatars.githubusercontent.com/u/93702146?v=4",
			"zzaekkii",
			"BACKEND",
			"Refactor: 서비스 레이어 리팩터링 및 예외 처리 개선",
			52,
			LocalDateTime.of(2025, 11, 7, 17, 30, 0));

		PrReviewSummary summary2 = new PrReviewSummary(
			"https://avatars.githubusercontent.com/u/93702146?v=4",
			"zzaekkii",
			"FRONTEND",
			"feat: 문서화 테스트 추가",
			38,
			LocalDateTime.of(2025, 11, 2, 22, 10, 0));

		PrReviewListResult result = new PrReviewListResult(List.of(summary1, summary2), true);

		given(prReviewService.getPrReviewList(any()))
			.willReturn(result);

		// when
		mockMvc.perform(get("/api/v1/pr-reviews/me")
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
			.param("status", "open")
			.param("order", "createdAt-desc")
			.param("position", "")
			.param("lastReviewId", "0")
			.param("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.prReviews").isArray())
			.andExpect(jsonPath("$.data.isLast").value(true))
			.andDo(document("내 PR 리뷰 요청글 전체 조회",
				resource(ResourceSnippetParameters.builder()
					.tag("💬 PR 리뷰 요청")
					.summary("내 PR 리뷰 요청글 전체 조회 API")
					.description("""
						로그인된 사용자의 PR 리뷰 요청글을 조회합니다.<br/>
						게시글 상태(open/closed), 정렬 기준(createdAt-desc, hitCount-desc), 직무 태그 필터링이 가능합니다.
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

		verify(prReviewService).getPrReviewList(
			argThat(condition -> condition.userId().equals(93702146L)));
	}

	@Test
	@WithMockMoyoyUser(id = 93702146L)
	@DisplayName("PR 리뷰 요청글 상세 조회 성공 - 200 OK")
	void getPrReviewDetail_success() throws Exception {
		// given
		long reviewId = 101L;

		PrReviewDetailResult result = new PrReviewDetailResult(
			"open",
			true,
			false,
			"https://avatars.githubusercontent.com/u/93702146?v=4",
			"zzaekkii",
			"BACKEND",
			"Refactor: 아키텍처 변경 + 도메인 모델과 JPA 엔티티 분리",
			128,
			LocalDateTime.of(2025, 11, 8, 0, 52, 0),
			null,
			"조회수 관리 문제를 어떻게 풀어낼 지 고민 중이라, 이 부분은 아직 구현하지 않았지만 코드 리뷰 부탁드립니다!",
			"https://github.com/Mo-yoy/Moyoy-Backend/pull/168");

		given(prReviewService.getPrReviewDetail(anyLong(), anyLong()))
			.willReturn(result);

		// when & then
		mockMvc.perform(get("/api/v1/pr-reviews/{pr-reviewId}", reviewId)
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.status").value("open"))
			.andExpect(jsonPath("$.data.title").value("Refactor: 아키텍처 변경 + 도메인 모델과 JPA 엔티티 분리"))
			.andExpect(jsonPath("$.data.username").value("zzaekkii"))
			.andExpect(jsonPath("$.data.isWriter").value(true))
			.andDo(document("PR 리뷰 요청글 상세 조회",
				resource(ResourceSnippetParameters.builder()
					.tag("💬 PR 리뷰 요청")
					.summary("PR 리뷰 요청글 상세 조회 API")
					.description("""
						특정 PR 리뷰 요청글의 상세 정보를 조회합니다.<br/>
						로그인한 사용자는 자신의 글인지 여부(`isWriter`)를 함께 확인할 수 있습니다.<br/>
						`isWriter`는 이후 수정, 삭제, 채택을 판단하기 위한 값입니다.
						""")
					.pathParameters(
						parameterWithName("pr-reviewId")
							.description("조회할 PR 리뷰 요청글 ID")
							.type(SimpleType.INTEGER))
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						fieldWithPath("data.status").description("요청글 상태 (open/closed)"),
						fieldWithPath("data.isWriter").description("현재 로그인한 사용자가 작성자인지 여부"),
						fieldWithPath("data.isAdopted").description("리뷰어를 채택했는지 여부"),
						fieldWithPath("data.profileImageUrl").description("작성자 프로필 이미지 URL"),
						fieldWithPath("data.username").description("작성자 이름"),
						fieldWithPath("data.position").description("요청글 직무태그"),
						fieldWithPath("data.title").description("요청글 제목"),
						fieldWithPath("data.hitCount").description("조회수"),
						fieldWithPath("data.createdAt").description("작성일자"),
						fieldWithPath("data.closedAt").description("마감일자").optional(),
						fieldWithPath("data.content").description("요청글 내용"),
						fieldWithPath("data.prUrl").description("PR 링크"))
					.build())));
	}

	@Test
	@WithMockMoyoyUser(id = 93702146L)
	@DisplayName("PR 리뷰 요청글 생성 성공 - 200 OK")
	void createPrReview_success() throws Exception {
		// given
		PrReviewCreateResult result = new PrReviewCreateResult(200L);

		given(prReviewService.createPrReview(any(), anyLong()))
			.willReturn(result);

		// when & then
		mockMvc.perform(post("/api/v1/pr-reviews")
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
			.contentType("application/json")
			.content("""
				    {
				      "title": "Feat: 조회수 중복 증가 방지 적용",
				      "position": "BACKEND",
				      "prUrl": "https://github.com/Mo-yoy/Moyoy-Backend/pull/200",
				      "content": "Redis 기반 조회수 중복 증가 방지 로직을 추가했습니다. 코드 리뷰 부탁드립니다!",
				      "closedAt": "2025-11-15T18:00:00"
				    }
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.prReviewId").value("200"))
			.andDo(document("PR 리뷰 요청글 생성",
				resource(ResourceSnippetParameters.builder()
					.tag("💬 PR 리뷰 요청")
					.summary("PR 리뷰 요청글 생성 API")
					.description("""
						새 PR 리뷰 요청글을 등록합니다.<br/>
						PR URL은 유효한 GitHub PR 링크 형식이어야 하고, 제목/내용에 최소 길이 제약이 있습니다.
						""")
					.requestFields(
						fieldWithPath("title").description("요청글 제목 (5~50자)"),
						fieldWithPath("position").description("직무 태그 (예: BACKEND, FRONTEND, IOS, ANDROID, DEVOPS)").optional(),
						fieldWithPath("prUrl").description("PR 링크 (예: https://github.com/org/repo/pull/123)"),
						fieldWithPath("content").description("요청글 내용 (10자 이상)"),
						fieldWithPath("closedAt").description("마감일자").optional())
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						fieldWithPath("data.prReviewId").description("생성된 PR 리뷰 요청글의 ID (리다이렉트 시 사용)"))
					.build())));
	}

	@Nested
	@DisplayName("PR 요청글 생성")
	class ValidationTest {

		@Test
		@WithMockMoyoyUser
		@DisplayName("잘못된 PR URL 형식 - 400 BAD REQUEST")
		void createPrReview_fail_invalidUrl() throws Exception {
			mockMvc.perform(post("/api/v1/pr-reviews")
				.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
				.contentType("application/json")
				.content("""
					    {
					      "title": "유효성 테스트",
					      "position": "BACKEND",
					      "prUrl": "http://invalid-url.com",
					      "content": "10자를 채워보겠습니다, 하하",
					      "closedAt": "2025-11-15T18:00:00"
					    }
					"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("유효한 GitHub PR URL")));
		}

		@Test
		@WithMockMoyoyUser
		@DisplayName("잘못된 직무 태그(position) - 400 BAD REQUEST")
		void invalidPosition_returnsBadRequest() throws Exception {
			mockMvc.perform(post("/api/v1/pr-reviews")
				.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
				.contentType("application/json")
				.content("""
						{
						  "title": "잘못된 직무 태그 테스트",
						  "position": "해킹",
						  "prUrl": "https://github.com/Mo-yoy/Moyoy-Backend/pull/200",
						  "content": "내용은 10자가 넘어야 하니께 한번 적어보자고",
						  "closedAt": "2025-11-15T18:00:00"
						}
					"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(PrReviewErrorCode.INVALID_POSITION.getCode()));
		}
	}

	@Test
	@WithMockMoyoyUser(id = 93702146L)
	@DisplayName("PR 리뷰 요청글 수정 성공 - 200 OK")
	void updatePrReview_success() throws Exception {
		// given
		long reviewId = 200L;
		PrReviewUpdateResult result = new PrReviewUpdateResult(reviewId);

		given(prReviewService.updatePrReview(eq(reviewId), any(), eq(93702146L)))
			.willReturn(result);

		// when & then
		mockMvc.perform(patch("/api/v1/pr-reviews/{pr-reviewId}", reviewId)
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
			.contentType("application/json")
			.content("""
				    {
				      "title": "Fix: 조회수 중복 증가 방지 버그 수정",
				      "position": "BACKEND",
				      "prUrl": "https://github.com/Mo-yoy/Moyoy-Backend/pull/201",
				      "content": "조회수 증가 로직에서 race condition을 해결했습니다.",
				      "closedAt": "2025-11-15T18:00:00"
				    }
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.prReviewId").value(reviewId))
			.andDo(document("PR 리뷰 요청글 수정",
				resource(ResourceSnippetParameters.builder()
					.tag("💬 PR 리뷰 요청")
					.summary("PR 리뷰 요청글 수정 API")
					.description("""
						기존에 작성한 PR 리뷰 요청글을 수정합니다.<br/>
						작성자 본인의 글만 수정할 수 있습니다.
						""")
					.pathParameters(
						parameterWithName("pr-reviewId").description("수정할 PR 리뷰 요청글 ID"))
					.requestFields(
						fieldWithPath("title").description("수정할 제목 (5~50자)"),
						fieldWithPath("position").description("직무 태그 (예: BACKEND, FRONTEND, IOS, ANDROID, DEVOPS)").optional(),
						fieldWithPath("prUrl").description("PR 링크 (예: https://github.com/org/repo/pull/123)"),
						fieldWithPath("content").description("요청글 내용 (10자 이상)"),
						fieldWithPath("closedAt").description("마감일자").optional())
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						fieldWithPath("data.prReviewId").description("수정된 PR 리뷰 요청글 ID"))
					.build())));
	}
}
