package com.umm.app.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RenewRefreshResponse {

    private String access;
    private String refresh;
}
