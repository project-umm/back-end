package com.umm.exception;

import lombok.Getter;

@Getter
public class AuthException extends BaseException{
    public AuthException(int statusCode, String message) {
        super(statusCode, message);
    }
}
