package com.moyoy.api.user.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.moyoy.api.user.application.UserService;

import com.moyoy.domain.ranking.RankingRepository;
import com.moyoy.domain.user.UserRepository;

import com.moyoy.infra.database.mysql.common.UserRankingQueryRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class UserProfileApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRankingQueryRepository userRankingQueryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RankingRepository rankingRepository;

	@Nested
	@DisplayName("유저 프로필 조회 API 호출 시")
	class GetUserProfile {

		@Test
		@DisplayName("유저가 존재하면 정상적으로 프로필을 반환한다")
		@WithMockUser(username = "testUser", roles = "USER")
		void success() throws Exception {

		}

		@Test
		@DisplayName("유저가 존재하지 않으면 404를 반환한다")
		@WithMockUser(username = "testUser", roles = "USER")
		void notFound() throws Exception {

		}

		@Test
		@DisplayName("비인증 사용자가 접근하면 401을 반환한다")
		void unauthorized() throws Exception {

		}
	}

}
