package com.umm.exception;

import com.umm.app.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException exc){

        // LOG LEVEL => INFO 시 StackTrace 출력
        if (log.isInfoEnabled()){
            exc.printStackTrace();
        };

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .message(exc.getMessage())
                .build();

        return ResponseEntity.status(HttpStatusCode.valueOf(exc.getStatusCode())).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exc){

        // LOG LEVEL => INFO 시 StackTrace 출력
        if (log.isInfoEnabled()){
            exc.printStackTrace();
        };

        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .message(exc.getMessage())
                .build();

        return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(errorResponse);
    }
}
