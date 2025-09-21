package com.moyoy.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserTest {

	@Test
	void 프로필_변경이_정상적으로_동작한다() {

		// given
		User user = User.builder()
			.id(1L)
			.githubUserId(12345)
			.username("oldName")
			.profileImgUrl("oldUrl")
			.socialSize(SocialSize.SMALL)
			.role(Role.USER)
			.build();

		// when
		user.changeProfile("newName", "newUrl");

		// then
		assertThat(user.getUsername()).isEqualTo("newName");
		assertThat(user.getProfileImgUrl()).isEqualTo("newUrl");
	}

	@Test
	void 소셜사이즈_변경이_정상적으로_동작한다() {

		// given
		User user = User.builder()
			.id(1L)
			.githubUserId(12345)
			.username("name")
			.profileImgUrl("url")
			.socialSize(SocialSize.SMALL)
			.role(Role.USER)
			.build();

		// when
		user.changeSocialSize(SocialSize.HUGE);

		// then
		assertThat(user.getSocialSize()).isEqualTo(SocialSize.HUGE);
	}
}
