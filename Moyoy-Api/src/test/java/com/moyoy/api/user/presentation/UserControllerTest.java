package com.moyoy.api.user.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.moyoy.common.constant.TestConstant.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.moyoy.api.common.ApiControllerAdvice;
import com.moyoy.api.user.application.UserService;
import com.moyoy.api.user.application.response.UserProfileResult;
import com.moyoy.common.annotation.WithMockMoyoyUser;

@WebMvcTest(value = UserController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
	OncePerRequestFilter.class})})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(ApiControllerAdvice.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Test
	@WithMockMoyoyUser(id = 1)
	void 유저_프로필_조회_문서화() throws Exception {

		UserProfileResult mockServiceResult = new UserProfileResult(
			1L,
			"moyoy",
			10000,
			"A",
			"http://~"
		);

		Mockito
			.when(userService.getUserProfile(anyLong()))
			.thenReturn(mockServiceResult);

		mockMvc
			.perform(
				get("/api/v1/users/me/profile")
					.header("Authorization", "Bearer " + MOCK_JWT_ACCESS_TOKEN))
			.andExpect(status().isOk())

			// REST Docs
			.andDo(document("개인 프로필 조회 성공",
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
						fieldWithPath("data.profileImgUrl").description("🖼️ 사용자 프로필 이미지 URL")
					)
					.build())));

	}

}
