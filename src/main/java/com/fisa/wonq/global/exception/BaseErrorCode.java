package com.fisa.wonq.global.exception;

import com.fisa.wonq.global.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    ErrorResponse getErrorResponse();

    String getMessage();

    HttpStatus getStatus();
}
