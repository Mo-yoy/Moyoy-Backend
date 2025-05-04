package com.moyo.backend.securityLegacy.oauth.dto;

import com.moyo.backend.user.Role;
import com.moyo.backend.user.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDto {

    private final Long id;
    private final Role role;

    public static UserDto userEntityToUserDto(User user){
        return new UserDto(user.getId(),user.getRole());
    }
}