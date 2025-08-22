package com.moyoy.domain.pr_review;

import java.util.Set;

public class SortableFields {
    public static final Set<String> ALLOWED = Set.of(
            "createdAt",
            "hitCount"
    );

    private SortableFields() {}
}
