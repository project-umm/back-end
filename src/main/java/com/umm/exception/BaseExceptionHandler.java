package com.umm.exception;

import com.umm.app.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException exc){
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .message(exc.getMessage())
                .build();

        return ResponseEntity.status(HttpStatusCode.valueOf(exc.getStatusCode())).body(errorResponse);
    }
}
