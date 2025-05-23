package com.umm.app.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ProfileResponse {

    private String profileUrl;
    private String nickname;
}
