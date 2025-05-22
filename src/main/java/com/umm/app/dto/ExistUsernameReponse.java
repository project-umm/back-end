package com.umm.app.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ExistUsernameReponse extends BaseResponse{

    private Boolean exist;
}
