package com.moyoy.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.Test;

class SocialSizeTest {

	@Test
	void following과_follower_수의_합계가_100미만이면_SMALL() {

		assertThat(SocialSize.of(0, 0)).isEqualTo(SocialSize.SMALL);
		assertThat(SocialSize.of(50, 49)).isEqualTo(SocialSize.SMALL);
	}

	@Test
	void following과_follower_수의_합계가_100이상_500미만이면_MEDIUM() {

		assertThat(SocialSize.of(50, 50)).isEqualTo(SocialSize.MEDIUM);
		assertThat(SocialSize.of(499, 0)).isEqualTo(SocialSize.MEDIUM);
	}

	@Test
	void following과_follower_수의_합계가_500이상_1000미만이면_LARGE() {

		assertThat(SocialSize.of(500, 0)).isEqualTo(SocialSize.LARGE);
		assertThat(SocialSize.of(999, 0)).isEqualTo(SocialSize.LARGE);
	}

	@Test
	void following과_follower_수의_합계가_1000이상이면_HUGE() {

		assertThat(SocialSize.of(500, 500)).isEqualTo(SocialSize.HUGE);
	}

}
