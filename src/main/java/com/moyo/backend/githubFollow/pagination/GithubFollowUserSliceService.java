package com.moyo.backend.githubFollow.pagination;

import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GithubFollowUserSliceService {

    public Slice<GithubFollowUser> getSlice(List<GithubFollowUser> users, long lastFetchedUserId, int pageSize) {

        List<GithubFollowUser> filteredList = users.stream()
                .filter(user -> lastFetchedUserId == 0 || user.id() > lastFetchedUserId)
                .limit(pageSize + 1)
                .collect(Collectors.toList());

        boolean hasNext = filteredList.size() > pageSize;

        if (hasNext) filteredList.removeLast();

        return new SliceImpl<>(filteredList, Pageable.unpaged(), hasNext);
    }
}
