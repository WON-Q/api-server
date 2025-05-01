package com.fisa.wonq.merchant.exception;

import com.fisa.wonq.global.exception.CustomException;

public class MenuException extends CustomException {
    public MenuException(MenuErrorCode errorCode) {
        super(errorCode);
    }
}
