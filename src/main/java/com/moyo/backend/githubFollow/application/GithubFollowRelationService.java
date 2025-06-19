package com.moyo.backend.githubFollow.application;

import com.moyo.backend.githubFollow.domain.GithubFollowRelation;
import com.moyo.backend.githubFollow.domain.GithubFollowRelationRepository;
import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import com.moyo.backend.githubFollow.dto.GithubFollowDetectRequest;
import com.moyo.backend.githubFollow.dto.GithubFollowDetectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.moyo.backend.githubFollow.util.GithubFollowUserSliceUtil.*;

@Service
@RequiredArgsConstructor
public class GithubFollowRelationService {

    private final GithubFollowRelationRepository githubFollowRelationRepository;

    public GithubFollowDetectResponse detectFollowUserList(Long userId, GithubFollowDetectRequest request){

        // forceSync 악용 검증 로직 추후 추가

        GithubFollowRelation githubFollowRelation = githubFollowRelationRepository.findByUserId(userId, request.isForceSync());
        List<GithubFollowUser> githubFollowUsers = githubFollowRelation.filterUsersByDetectType(request.getDetectType());

        Slice<GithubFollowUser> pagedSlice = getSlice(githubFollowUsers, request.getLastFetchedUserId(), request.getPagingSize());

        return GithubFollowDetectResponse.of(pagedSlice, githubFollowRelation.getCreatedAt(), githubFollowUsers.size());
    }
}
