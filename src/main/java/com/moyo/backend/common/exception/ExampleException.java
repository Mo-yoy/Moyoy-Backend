package com.moyo.backend.common.exception;

public class ExampleException extends MoyoException{

    public static final MoyoException EXCEPTION = new ExampleException();
    private ExampleException() {
        super(CommonErrorCode.UNKNOWN_INTERNAL_SERVER_ERROR);
    }
}
