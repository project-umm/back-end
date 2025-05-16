package com.umm.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class BaseException extends RuntimeException {

    private final int statusCode;

    public BaseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
