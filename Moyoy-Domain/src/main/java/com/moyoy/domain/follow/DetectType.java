package com.moyoy.domain.follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DetectType {

	MUTUAL("mutual"),
	FOLLOW_ONLY("follow-only"),
	FOLLOWED_ONLY("followed-only");

	private final String value;

	public static DetectType fromValue(String value) {
		for (DetectType type : values()) {
			if (type.getValue().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException();
	}
}
