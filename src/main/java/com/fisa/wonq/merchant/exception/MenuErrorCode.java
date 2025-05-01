package com.fisa.wonq.merchant.exception;

import com.fisa.wonq.global.exception.BaseErrorCode;
import com.fisa.wonq.global.response.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MenuErrorCode implements BaseErrorCode {
    UNAUTHORIZED_MENU_ACCESS(HttpStatus.UNAUTHORIZED, "MENU403", "해당 메뉴에 대한 접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.of(code, message);
    }
}
