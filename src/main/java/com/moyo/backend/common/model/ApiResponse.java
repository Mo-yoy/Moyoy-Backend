package com.moyo.backend.common.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.moyo.backend.common.constant.MoyoConstants.NO_CONTENT;
import static com.moyo.backend.common.constant.MoyoConstants.OK;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"status", "code", "message", "data"})
public class ApiResponse <T>{

    private final int status;

    private final String code;

    private final String message;

    private final T data;

    public static <S> ApiResponse<S> success(S data){

        return new ApiResponse<>(OK, "OK", null, data);
    }

    public static <S> ApiResponse<S> noContent(){

        return new ApiResponse<>(NO_CONTENT, "NO_CONTENT", null, null);
    }
}
