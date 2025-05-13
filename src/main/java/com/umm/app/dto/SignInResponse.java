package com.umm.app.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class SignInResponse {

    private String grantType;
    private String access;
    private String refresh;
}
