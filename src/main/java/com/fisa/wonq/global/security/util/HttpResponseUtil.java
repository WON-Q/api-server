package com.fisa.wonq.global.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisa.wonq.global.exception.BaseErrorCode;
import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HttpResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void setSuccessResponse(HttpServletResponse response, HttpStatus httpStatus, Object body)
            throws IOException {
        String responseBody = objectMapper.writeValueAsString(ApiResponse.of(body));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }

    public static void writeErrorResponse(HttpServletResponse response, BaseErrorCode errorCode) throws
            IOException {
        final ErrorResponse errorResponse = errorCode.getErrorResponse();
        String responseBody = objectMapper.writeValueAsString(errorResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode.getStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
