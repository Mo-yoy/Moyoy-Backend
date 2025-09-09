package com.moyoy.api.github_follow.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.moyoy.common.constant.TestConstant.MOCK_JWT_ACCESS_TOKEN;
import static com.moyoy.domain.support.error.github.GithubErrorCode.*;
import static com.moyoy.domain.support.error.github_follow.FollowErrorCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import com.moyoy.api.github_follow.application.GithubFollowCommandService;
import com.moyoy.api.github_follow.application.GithubFollowDetectService;
import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;

import com.moyoy.domain.github_follow.GithubUser;
import com.moyoy.domain.support.error.CommonErrorCode;
import com.moyoy.domain.support.error.github_follow.FollowErrorCode;
import com.moyoy.domain.support.error.github_follow.GithubFollowSnapshotCoolDownNotExpiredException;
import com.moyoy.domain.support.page.SliceResult;

import com.moyoy.common.annotation.ControllerTest;
import com.moyoy.common.annotation.WithMockMoyoyUser;

@ControllerTest(controllers = GithubFollowController.class)
class GithubFollowControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GithubFollowDetectService githubFollowDetectService;

	@MockitoBean
	private GithubFollowCommandService githubFollowCommandService;

	@WithMockMoyoyUser
	@Test
	void 맞팔탐지기_조회_성공_200_OK() throws Exception {

		// given
		GithubUser user1 = new GithubUser(12345, "username1", "http://profile.image/1");
		GithubUser user2 = new GithubUser(67890, "username2", "http://profile.image/2");

		List<GithubUser> userList = List.of(user1, user2);
		int totalUserCount = userList.size();
		LocalDateTime createdAt = LocalDateTime.of(2025, 8, 28, 16, 16, 10, 944301811);

		SliceResult<GithubUser> userSlice = SliceResult.of(userList, false);

		Optional<GithubFollowDetectionResult> result = Optional.of(new GithubFollowDetectionResult(userSlice, createdAt, totalUserCount));

		given(githubFollowDetectService.detect(anyLong(), any())).willReturn(result);

		// when
		mockMvc.perform(get("/api/v1/users/me/followings/{detectType}", "mutual")
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
			.param("lastGithubUserId", "22")
			.param("size", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userList").isArray())
			.andExpect(jsonPath("$.data.totalUserCount").value(2))
			.andExpect(jsonPath("$.data.lastPage").value(true))
			.andExpect(jsonPath("$.data.lastSyncAt").exists())
			.andDo(document("맞팔탐지기 조회 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("👥 깃허브 팔로우 관계 탐지")
					.summary("깃허브 팔로우 관계 탐지 API")
					.description("""
						현재 로그인한 사용자의 Github 팔로워/팔로잉 목록에서
						요청한 detectType(맞팔로우, 나만 팔로우, 상대만 나를 팔로우)에 해당하는 사용자 목록을 조회합니다.
						<br/>
						캐시를 활용해 성능을 최적화합니다.
						- 캐시에 데이터가 존재하는 경우: 서버는 즉시 `200 OK`와 함께 결과를 반환합니다.
						- 캐시에 데이터가 없는 경우: 서버는 `202 Accepted`를 반환하고, 백그라운드 작업을 통해 데이터를 캐시에 적재합니다.
						  클라이언트는 잠시 대기 후 동일 요청을 다시 시도하면, 이후에는 `200 OK`와 함께 캐시된 결과를 받을 수 있습니다.
						""")
					.pathParameters(
						parameterWithName("detectType")
							.description("mutual  (맞팔로우)<br/> follow-only  (나만 상대를 팔로우)<br/> followed-only (상대만 나를 팔로우)")
							.type(SimpleType.STRING)
							.defaultValue("mutual"))
					.queryParameters(
						parameterWithName("lastGithubUserId")
							.description("이전 페이지에서 조회한 회원중 마지막 회원의 GithubUserId ,해당 파라미터는 비워둘 시 첫 페이지 조회로 간주 합니다. ")
							.type(SimpleType.INTEGER)
							.optional(),
						parameterWithName("size")
							.description("조회할 사용자 수 (default: 30)")
							.type(SimpleType.INTEGER)
							.optional())
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						fieldWithPath("data.userList").description("👤 탐지된 사용자 목록"),
						fieldWithPath("data.userList[].githubUserId").description("사용자 Github ID"),
						fieldWithPath("data.userList[].username").description("사용자 이름"),
						fieldWithPath("data.userList[].profileImgUrl").description("사용자 프로필 이미지 URL"),
						fieldWithPath("data.totalUserCount").description("📊 총 사용자 수"),
						fieldWithPath("data.lastPage").description("📌 마지막 페이지 여부"),
						fieldWithPath("data.lastSyncAt").description("⏱ 마지막 싱크 시간 타임 스탬프"))
					.build())));
	}

	@WithMockMoyoyUser
	@Test
	void 맞팔탐지기_조회_성공_202_ACCEPTED() throws Exception {

		// given
		Optional<GithubFollowDetectionResult> result = Optional.empty();
		given(githubFollowDetectService.detect(anyLong(), any())).willReturn(result);

		// when
		mockMvc.perform(get("/api/v1/users/me/followings/{detectType}", "mutual")
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
			.param("lastGithubUserId", "22")
			.param("size", "1"))
			.andExpect(status().isAccepted())
			.andDo(document("맞팔탐지기 조회를 위한 데이터 수집 요청 완료",
				resource(ResourceSnippetParameters.builder()
					.tag("👥 깃허브 팔로우 관계 탐지")
					.summary("깃허브 팔로우 관계 탐지 API")
					.pathParameters(
						parameterWithName("detectType")
							.description("mutual  (맞팔로우)<br/> follow-only  (나만 상대를 팔로우)<br/> followed-only (상대만 나를 팔로우)")
							.type(SimpleType.STRING)
							.defaultValue("mutual"))
					.queryParameters(
						parameterWithName("lastGithubUserId")
							.description("이전 페이지에서 조회한 회원중 마지막 회원의 GithubUserId ,해당 파라미터는 비워둘 시 첫 페이지 조회로 간주 합니다. ")
							.type(SimpleType.INTEGER)
							.optional(),
						parameterWithName("size")
							.description("조회할 사용자 수 (default: 30)")
							.type(SimpleType.INTEGER)
							.optional())
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						subsectionWithPath("data").description("🚫 데이터 없음 (null)").optional())
					.build())));
	}

	@Nested
	@DisplayName("Github Follow Detect API Validation 테스트")
	class GithubFollowDetectValidationTests {

		@Test
		@WithMockMoyoyUser
		@DisplayName("detectType이 mutual|follow-only|followed-only가 아닌 값이면 400 Bad Request 반환")
		void invalidDetectType_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/users/me/followings/invalidType")
				.param("lastGithubUserId", "10")
				.param("size", "20"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_PARAM.getCode()));
		}

		@Test
		@WithMockMoyoyUser
		@DisplayName("size가 0 이하일 경우 400 Bad Request 반환")
		void sizeTooSmall_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/users/me/followings/mutual")
				.param("lastGithubUserId", "10")
				.param("size", "0"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_PARAM.getCode()));
		}

		@Test
		@WithMockMoyoyUser
		@DisplayName("size가 100 초과일 경우 400 Bad Request 반환")
		void sizeTooLarge_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/users/me/followings/mutual")
				.param("lastGithubUserId", "10")
				.param("size", "101"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_PARAM.getCode()));
		}

		@Test
		@WithMockMoyoyUser
		@DisplayName("lastGithubUserId가 음수일 경우 400 Bad Request 반환")
		void lastGithubUserIdNegative_returnsBadRequest() throws Exception {
			mockMvc.perform(get("/api/v1/users/me/followings/mutual")
				.param("lastGithubUserId", "-1")
				.param("size", "20"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_PARAM.getCode()));
		}

		@Test
		@WithMockMoyoyUser
		@DisplayName("lastGithubUserId와 size를 주지 않으면 기본값이 적용된다")
		void nullValues_defaultsApplied() throws Exception {

			// given
			given(githubFollowDetectService.detect(anyLong(),
				argThat(data -> data.lastGithubUserId() == 0 && data.size() == 30)))
				.willReturn(Optional.empty());

			// when & then
			mockMvc.perform(get("/api/v1/users/me/followings/mutual"))
				.andExpect(status().isAccepted());

			verify(githubFollowDetectService).detect(anyLong(),
				argThat(data -> data.lastGithubUserId() == 0 && data.size() == 30));
		}
	}

	@WithMockMoyoyUser
	@Test
	void 맞팔탐지기_강제갱신_202() throws Exception {

		// given
		willDoNothing()
			.given(githubFollowDetectService)
			.refresh(anyLong());

		// when
		mockMvc.perform(post("/api/v1/users/me/followings/refresh")
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN))
			.andExpect(status().isAccepted())

			// REST Docs
			.andDo(document("맞팔탐지기 조회를 위한 데이터 강제 갱신 요청",
				resource(ResourceSnippetParameters.builder()
					.tag("👥 깃허브 팔로우 관계 탐지")
					.summary("깃허브 팔로우 관계 탐지기 캐시 강제 갱신 요청")
					.description("현재 사용자의 요청을 처리하기 위한 데이터 강제 갱신 요청, 캐시에 데이터가 이미 존재하면 강제갱신, 데이터가 없으면 수집")
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						subsectionWithPath("data").description("🚫 데이터 없음 (null)").optional())
					.build())));
	}

	@WithMockMoyoyUser
	@Test
	@DisplayName("맞팔 탐지기 리프레시 시도시, 5분 이내에 갱신한 적이 있으면 실패 응답 문서화")
	void refreshWithin5Minutes_fails() throws Exception {

		// given
		doThrow(new GithubFollowSnapshotCoolDownNotExpiredException())
			.when(githubFollowDetectService).refresh(anyLong());

		// when & then
		mockMvc.perform(post("/api/v1/users/me/followings/refresh")
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(FollowErrorCode.SNAPSHOT_COOLDOWN_NOT_EXPIRED.getStatus()))
			.andExpect(jsonPath("$.code").value(FollowErrorCode.SNAPSHOT_COOLDOWN_NOT_EXPIRED.getCode()))

			.andDo(document("github-follow-refresh-fail",
				resource(ResourceSnippetParameters.builder()
					.tag("👥 깃허브 팔로우 관계 탐지")
					.summary("깃허브 팔로우 관계 탐지기 캐시 강제 갱신 요청")
					.description("현재 사용자의 요청을 처리하기 위한 데이터 강제 갱신 요청, 캐시에 데이터가 이미 존재하면 강제갱신, 데이터가 없으면 수집")
					.responseFields(
						fieldWithPath("status").description("❌ 응답 상태 코드 (HTTP status)"),
						fieldWithPath("code").description("🔢 도메인 에러 코드"),
						fieldWithPath("message").description("📝 상세 에러 메시지"),
						fieldWithPath("data").description("🚫 데이터 없음 (null)").optional())
					.build())));
	}

	@Test
	void 팔로우_성공_문서화() throws Exception {

		willDoNothing().given(githubFollowCommandService).follow(anyLong(), anyInt());

		mockMvc.perform(post("/api/v1/follow/{targetUserId}", 12345L)
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204))
			.andExpect(jsonPath("$.code").value("NO_CONTENT"))
			.andExpect(jsonPath("$.message").isEmpty())
			.andExpect(jsonPath("$.data").isEmpty())
			.andDo(document("팔로우 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("👥 깃허브 팔로우 관계 탐지")
					.summary("깃허브 사용자 팔로우 API")
					.description("현재 로그인한 사용자가 targetUserId에 해당하는 깃허브 상 사용자를 팔로우합니다.")
					.pathParameters(
						parameterWithName("targetUserId").description("팔로우 대상 사용자 ID"))
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지 (비어 있을 수 있음)").optional(),
						fieldWithPath("data").description("🚫 데이터 없음 (null)").optional())
					.build())));
	}

	@Test
	void 언팔로우_성공_문서화() throws Exception {

		willDoNothing().given(githubFollowCommandService).unfollow(anyLong(), anyInt());

		mockMvc.perform(delete("/api/v1/unfollow/{targetUserId}", 12345L)
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204)) // noContent 응답 코드
			.andExpect(jsonPath("$.code").value("NO_CONTENT"))
			.andExpect(jsonPath("$.message").isEmpty())
			.andExpect(jsonPath("$.data").isEmpty())
			.andDo(document("언팔로우 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("👥 깃허브 팔로우 관계 탐지")
					.summary("깃허브 사용자 언팔로우 API")
					.description("현재 로그인한 사용자가 targetUserId에 해당하는 깃허브 사용자를 언 팔로우합니다.")
					.pathParameters(
						parameterWithName("targetUserId").description("언 팔로우 대상 사용자 ID"))
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지 (비어 있을 수 있음)").optional(),
						fieldWithPath("data").description("🚫 데이터 없음 (null)").optional())
					.build())));
	}

}
