package com.moyoy.api.user.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.moyoy.common.constant.TestConstant.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import com.moyoy.api.common.ApiControllerAdvice;
import com.moyoy.api.user.application.UserService;
import com.moyoy.api.user.application.response.UserProfileQueryResult;

import com.moyoy.common.annotation.WithMockMoyoyUser;

@WebMvcTest(controllers = UserController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = OncePerRequestFilter.class)})
@Import(ApiControllerAdvice.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(RestDocumentationExtension.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Test
	@WithMockMoyoyUser(id = 1234)
	void 유저_프로필_조회_API_문서화를_성공한다() throws Exception {

		UserProfileQueryResult mockProfileResult = new UserProfileQueryResult(
			1234L,
			"moyoy",
			10000,
			"A",
			"https://~");

		when(userService.getUserProfile(1234L))
			.thenReturn(mockProfileResult);

		mockMvc
			.perform(
				get("/api/v1/users/me/profile")
					.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userId").value(1234L))
			.andExpect(jsonPath("$.data.username").value("moyoy"))
			.andExpect(jsonPath("$.data.rankPoint").value(10000))
			.andExpect(jsonPath("$.data.rankGrade").value("A"))
			.andExpect(jsonPath("$.data.profileImgUrl").value("https://~"))

			// Docs
			.andDo(document("사용자 프로필 조회 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("🙋 유저 프로필")
					.summary("유저 개인 프로필 조회 API")
					.description("유저의 프로필 및 관련 데이터를 조회합니다.")
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						fieldWithPath("data.userId").description("👦 사용자 ID"),
						fieldWithPath("data.username").description("🙋 사용자 이름"),
						fieldWithPath("data.rankPoint").description("🌟 사용자 랭킹 점수"),
						fieldWithPath("data.rankGrade").description("📊 사용자 랭킹 등급"),
						fieldWithPath("data.profileImgUrl").description("🖼️ 사용자 프로필 이미지 URL"))
					.build())));
	}
}
