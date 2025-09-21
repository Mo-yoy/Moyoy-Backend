package com.moyoy.api.support;

import java.util.List;
import java.util.function.ToIntFunction;

import com.moyoy.common.page.SliceResult;

public class SlicePagingUtils {

    public static <T> SliceResult<T> window(List<T> sorted, int lastId, int size, ToIntFunction<T> idExtractor) {

        // lastId == 0 이면 처음 페이지
        List<T> pagePlusOne = sorted.stream()
                .filter(it -> lastId == 0 || idExtractor.applyAsInt(it) > lastId)
                .limit(size + 1L)
                .toList();

        boolean hasNext = pagePlusOne.size() > size;
        List<T> page = hasNext ? pagePlusOne.subList(0, size) : pagePlusOne;

        return new SliceResult<>(page, !hasNext, hasNext);
    }
}