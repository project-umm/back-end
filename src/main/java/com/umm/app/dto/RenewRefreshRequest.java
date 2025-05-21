package com.umm.app.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RenewRefreshRequest {

    private String refresh;
}
