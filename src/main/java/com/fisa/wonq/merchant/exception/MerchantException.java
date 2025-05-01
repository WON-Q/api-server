package com.fisa.wonq.merchant.exception;


import com.fisa.wonq.global.exception.CustomException;

public class MerchantException extends CustomException {

    public MerchantException(MerchantErrorCode errorCode) {
        super(errorCode);
    }
}
