package com.umm.app.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageableDmChatResponse extends BasePageableResponse{

    private List<Chats> chats;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Chats {
        private String profileUrl;
        private String nickname;
        private String username;
        private String chatMessage;
        private Long createdAt;
    }
}
