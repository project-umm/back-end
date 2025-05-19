package com.umm.app.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ExistUsernameRequest {

    private String username;
}
