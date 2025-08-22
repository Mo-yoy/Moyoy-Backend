package com.moyoy.domain.pr_review;

import com.moyoy.domain.support.error.pr_review.StatusNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Status {
    OPEN("open"),
    CLOSED("closed");

    private final String value;

    public static Status from(String value) {
        return Arrays.stream(Status.values())
                .filter(status -> status.name().equalsIgnoreCase(value) || status.value.equals(value))
                .findFirst()
                .orElseThrow(StatusNotFoundException::new);
    }

    public boolean isclosed() {
        return this == CLOSED;
    }
}
