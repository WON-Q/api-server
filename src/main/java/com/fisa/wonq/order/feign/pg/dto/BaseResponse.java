package com.fisa.wonq.order.feign.pg.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    private Boolean isSuccess;

    private int statusCode;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

}

