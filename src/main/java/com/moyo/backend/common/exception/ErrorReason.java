package com.moyo.backend.common.exception;

import lombok.*;

@Getter
@AllArgsConstructor
public class ErrorReason {

    private Integer status;
    private String code;
    private String errorMessage;
}