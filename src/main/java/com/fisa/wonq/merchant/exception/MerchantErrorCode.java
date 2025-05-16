package com.fisa.wonq.merchant.exception;

import com.fisa.wonq.global.exception.BaseErrorCode;
import com.fisa.wonq.global.response.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MerchantErrorCode implements BaseErrorCode {

    MERCHANT_NOT_FOUND(HttpStatus.NOT_FOUND, "MERCHANT404", "존재하지 않는 가맹점입니다."),
    TABLE_NOT_FOUND(HttpStatus.NOT_FOUND, "TABLE404", "존재하지 않는 테이블입니다."),
    DUPLICATE_TABLE_NUMBER(HttpStatus.BAD_REQUEST, "TABLE4002", "이미 동일한 테이블 번호가 존재합니다."),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "OPTION404", "존재하지 않는 옵션입니다."),
    ORDER_MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "OPTIONMENU404", "존재하지 않는 메뉴 옵션입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS403", "접근할 수 있는 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.of(code, message);
    }
}
