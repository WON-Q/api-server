package com.fisa.wonq.global.security.exception;

import com.fisa.wonq.global.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public class SecurityException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public SecurityException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
