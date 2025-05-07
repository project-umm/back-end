package com.umm.app.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class SignUpRequest {

    private String username;
    private String nickname;
    private String password;
}
