package com.moyo.backend.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moyo.backend.common.exception.ErrorReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.moyo.backend.common.constant.MoyoConstants.NO_CONTENT;
import static com.moyo.backend.common.constant.MoyoConstants.OK;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"status", "code", "message", "data"})
@Schema(description = "API 공통 최종 응답 객체")
public class ApiResponse <T>{

    @Schema(description = "HTTP 상태 코드", example = "HTTP 상태 코드")
    private final int status;

    @Schema(description = "응답 코드", example = "커스텀 응답 코드")
    private final String code;

    @Schema(description = "추가 설명 메시지", example = "해당 요청 처리에 대한 추가 설명 메시지")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T data;

    public static <S> ApiResponse<S> success(S data){

        return new ApiResponse<>(OK, "OK", null, data);
    }

    public static <S> ApiResponse<S> noContent(){

        return new ApiResponse<>(NO_CONTENT, "NO_CONTENT", null, null);
    }

    public static <S> ApiResponse<S> fail(ErrorReason errorReason){

        return new ApiResponse<>(errorReason.getStatus(), errorReason.getCode(), errorReason.getErrorMessage(), null);
    }
}
