package com.umm.app.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StompErrorResponse extends ErrorResponse {

    private Integer statusCode;
}
