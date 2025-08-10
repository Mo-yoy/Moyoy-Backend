package com.moyoy.api.auth.jwt.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.moyoy.common.exception.auth.AuthErrorCode.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import com.moyoy.api.auth.jwt.business.JwtReissueService;
import com.moyoy.api.auth.jwt.business.ReissuedTokens;
import com.moyoy.api.common.handler.GlobalExceptionHandler;
import com.moyoy.api.common.util.CookieUtils;
import com.moyoy.common.exception.MoyoException;
import com.moyoy.common.exception.auth.AuthErrorCode;

import jakarta.servlet.http.Cookie;

@WebMvcTest(value = JwtReissueController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(GlobalExceptionHandler.class)
class JwtReissueControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtReissueService jwtReissueService;

	@MockitoBean
	private CookieUtils cookieUtils;

	@Test
	void 쿠키에_유효한_리프레시_토큰을_전달해서_토큰_재발급을_진행_할_수있다() throws Exception {

		// given
		String fakeRefreshToken = "fakeRefreshTokenValue";
		ReissuedTokens reissuedTokens = new ReissuedTokens("newAccessToken", "newRefreshToken");

		given(jwtReissueService.reIssueJwt(fakeRefreshToken)).willReturn(reissuedTokens);
		given(cookieUtils.createJwtRefreshTokenCookie(reissuedTokens.refreshToken())).willReturn(ResponseCookie.from("refresh", "newRefreshToken").build());

		// when
		mockMvc.perform(post("/api/v1/auth/reissue/token")
			.cookie(new Cookie("refresh", fakeRefreshToken)))

			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").value("newAccessToken"))
			.andExpect(header().exists("Set-Cookie")) // 응답 헤더에 쿠키가 포함됐는지 확인

			// Docs
			.andDo(document("JWT 재발급 성공",
				resource(ResourceSnippetParameters.builder()
					.tag("🔄 JWT 재발급")
					.summary("JWT 토큰 재발급 API")
					.description("사용자의 요청에 포함된 Cookie 중 refresh={리프레시 토큰}을 이용해 토큰 재발급을 수행하며, 새로운 Access 토큰은 응답 본문에, Refresh 토큰은 Set-Cookie 헤더를 통해 발급합니다. \n 자세한 사용법은 디스코드를 참고 해 주세요")
					.responseFields(
						fieldWithPath("status").description("✅ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 응답 코드"),
						fieldWithPath("message").description("📝 응답 메시지"),
						fieldWithPath("data.accessToken").description("🔑 새로 발급된 Access Token"))
					.build())));
	}

	@ParameterizedTest(name = "{index} => errorCode={0}")
	@MethodSource("jwtReissueErrorCodes")
	void 토큰재발급_에러코드_문서화(AuthErrorCode errorCode) throws Exception {

		doThrow(new MoyoException(errorCode)).when(jwtReissueService).reIssueJwt(anyString());

		mockMvc.perform(post("/api/v1/auth/reissue/token"))
			.andExpect(status().is(errorCode.getStatus()))
			.andExpect(jsonPath("$.code").value(errorCode.getCode()))
			.andExpect(jsonPath("$.message").value(errorCode.getMessage()))
			.andDo(document(errorCode.getCode(),
				resource(ResourceSnippetParameters.builder()
					.tag("🔄 JWT 재발급")
					.responseFields(
						fieldWithPath("status").description("❌ 응답 상태 코드"),
						fieldWithPath("code").description("🔢 에러 코드"),
						fieldWithPath("message").description("📝 에러 메시지"),
						subsectionWithPath("data").description("🚫 데이터 없음").optional())
					.build())));
	}

	static Stream<AuthErrorCode> jwtReissueErrorCodes() {
		return Stream.of(
			TOKEN_NOT_EXIST,
			TOKEN_TYPE_MISMATCH,
			INVALID_TOKEN,
			EXPIRED_TOKEN,
			BLOCKED_TOKEN);
	}

}
