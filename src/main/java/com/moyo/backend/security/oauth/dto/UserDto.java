package com.moyo.backend.security.oauth.dto;

import com.moyo.backend.domain.user.Role;
import com.moyo.backend.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDto {

    private final Role role;
    private final String providerId;

    public static UserDto from(User user){
        return new UserDto(user.getRole(),user.getProviderId());
    }
}