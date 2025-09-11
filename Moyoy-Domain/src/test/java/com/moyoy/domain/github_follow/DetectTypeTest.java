package com.moyoy.domain.github_follow;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DetectTypeTest {

	@Test
	void value가_mutual이면_ENUM_MUTUAL이_리턴된다() {

		assertThat(DetectType.fromValue("mutual")).isEqualTo(DetectType.MUTUAL);
	}

	@Test
	void value가_follow_only면_ENUM_FOLLOW_ONLY가_리턴된다() {

		assertThat(DetectType.fromValue("follow-only")).isEqualTo(DetectType.FOLLOW_ONLY);
	}

	@Test
	void value가_followed_only면_ENUM_FOLLOWED_ONLY가_리턴된다() {

		assertThat(DetectType.fromValue("followed-only")).isEqualTo(DetectType.FOLLOWED_ONLY);
	}

	@Test
	void 대소문자를_무시하고_DETECT_TYPE_ENUM을_찾을수있다() {

		assertThat(DetectType.fromValue("MUTUAL")).isEqualTo(DetectType.MUTUAL);
		assertThat(DetectType.fromValue("FOLLOW-ONLY")).isEqualTo(DetectType.FOLLOW_ONLY);
		assertThat(DetectType.fromValue("FOLLOWED-ONLY")).isEqualTo(DetectType.FOLLOWED_ONLY);
	}

	@Test
	void 잘못된_value면_예외가_발생한다() {
		assertThatThrownBy(() -> DetectType.fromValue("invalid"))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
