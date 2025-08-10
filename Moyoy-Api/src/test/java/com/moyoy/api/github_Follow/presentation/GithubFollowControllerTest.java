package com.moyoy.api.github_Follow.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.moyoy.common.constant.TestConstant.MOCK_JWT_ACCESS_TOKEN;
import static com.moyoy.common.exception.github_follow.FollowErrorCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

import com.moyoy.api.common.handler.GlobalExceptionHandler;
import com.moyoy.api.github_follow.business.GithubFollowDetectionResult;
import com.moyoy.api.github_follow.business.GithubFollowService;
import com.moyoy.api.github_follow.presentation.GithubFollowController;
import com.moyoy.common.annotation.WithMockMoyoyUser;
import com.moyoy.common.exception.MoyoException;
import com.moyoy.common.exception.github_follow.FollowErrorCode;
import com.moyoy.domain.follow.GithubFollowUser;

@WebMvcTest(value = GithubFollowController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(GlobalExceptionHandler.class)
class GithubFollowControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GithubFollowService githubFollowService;

	@WithMockMoyoyUser
	@Test
	void 맞팔탐지기_성공() throws Exception {

		// given
		GithubFollowUser user1 = new GithubFollowUser(12345, "username1", "http://profile.image/1");
		GithubFollowUser user2 = new GithubFollowUser(67890, "username2", "http://profile.image/2");

		List<GithubFollowUser> userList = List.of(user1, user2);
		int totalUserCount = userList.size();
		LocalDateTime createdAt = LocalDateTime.now().minusMinutes(5);

		Slice<GithubFollowUser> userSlice = new SliceImpl<>(userList);

		GithubFollowDetectionResult result = new GithubFollowDetectionResult(userSlice, createdAt, totalUserCount);

		// Mockito 빈을 사용하는데 해당 빈의 어떤 메서드에 어떤 입력을 넣었을 때 원하는 응답이 오도록 조절함.
		given(githubFollowService.getFollowUserSlice(anyLong(), any())).willReturn(result);

		// when
		mockMvc.perform(get("/api/v1/users/me/followings/{detectType}", "mutual") // 어떤 입력을 넣어도 Request DTO로 취합
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
			.param("lastGithubUserId", "22") // 어떤 입력을 넣어도 Reqeust DTO로 취합
			.param("pageSize", "1")
			.param("forceSync", "false"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userList").isArray())
			.andExpect(jsonPath("$.data.totalUserCount").value(2))
			.andExpect(jsonPath("$.data.lastPage").value(true))

			// REST Docs
			.andDo(document("맞팔탐지기 조회 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("👥 깃허브 팔로우 관계 탐지")
					.summary("깃허브 팔로우 관계 탐지 API")
					.description("현재 로그인한 사용자의 Github상 팔로워, 팔로잉 목록 데이터에서 사용자가 입력한 detectType(맞팔로우, 나만 팔로우, 상대만 나를 팔로우)를 기준으로 사용자 목록을 조회합니다. <br/><br/> 깃허브 OAuth를 이용한 로그인을 한 번이라도 진행한 적이 있어야 사용 가능합니다.")
					.pathParameters(
						parameterWithName("detectType").description("mutual  (맞팔로우)<br/> follow-only  (나만 상대를 팔로우)<br/> followed-only (상대만 나를 팔로우)").type(SimpleType.STRING).defaultValue("mutual"))
					.queryParameters(
						parameterWithName("lastGithubUserId").description("이전 페이지에서 조회한 회원중 마지막 회원의 GithubUserId ,해당 파라미터는 비워둘 시 첫 페이지 조회로 간주 합니다. ").type(SimpleType.INTEGER).optional(),
						parameterWithName("pageSize").description("조회할 사용자 수 (default: 30)").type(SimpleType.INTEGER).optional(),
						parameterWithName("forceSync").description("강제 동기화 여부 (default: false)").type(SimpleType.BOOLEAN).optional())
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
						fieldWithPath("data.lastSyncAt").description("⏱ 마지막 싱크 시간 (x 분전)"))
					.build())));

	}

	@WithMockMoyoyUser
	@ParameterizedTest(name = "{index} => errorCode={0}")
	@MethodSource("followDetectorErrorCodes")
	void 맞팔탐지기_에러코드_문서화(FollowErrorCode errorCode) throws Exception {
		// given
		doThrow(new MoyoException(errorCode)).when(githubFollowService).getFollowUserSlice(anyLong(), any());

		// when & then
		mockMvc.perform(get("/api/v1/users/me/followings/{detectType}", "mutual") // 어떤 입력을 넣어도 Request DTO로 취합
			.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN)
			.param("targetGithubUserId", "22") // 어떤 입력을 넣어도 Reqeust DTO로 취합
			.param("pageSize", "1"))
			.andExpect(status().is(errorCode.getStatus()))
			.andExpect(jsonPath("$.code").value(errorCode.getCode()))
			.andExpect(jsonPath("$.message").value(errorCode.getMessage()))

			.andDo(document(errorCode.getCode(),
				resource(ResourceSnippetParameters.builder()
					.tag("👥 깃허브 팔로우 관계 탐지")
					.pathParameters(
						parameterWithName("detectType").description("mutual  (맞팔로우)<br/> follow-only  (나만 상대를 팔로우)<br/> followed-only (상대만 나를 팔로우)"))
					.responseFields(
						fieldWithPath("status").description("❌ 응답 상태 코드 (HTTP status)"),
						fieldWithPath("code").description("🔢 도메인 에러 코드"),
						fieldWithPath("message").description("📝 상세 에러 메시지"),
						subsectionWithPath("data").description("🚫 데이터 없음 (null)").optional())
					.build())));
	}

	static Stream<FollowErrorCode> followDetectorErrorCodes() {
		return Stream.of(
			LIMIT_EXCEED);
	}

	@Test
	void 팔로우_성공() throws Exception {

		willDoNothing().given(githubFollowService).follow(anyLong(), anyInt());

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
	void 언팔로우_성공() throws Exception {

		willDoNothing().given(githubFollowService).unfollow(anyLong(), anyInt());

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
