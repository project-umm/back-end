package com.umm.app.dto;

import com.umm.app.entity.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class PageableUserResponse {

    List<Users> users;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    public static class Users {
        private String profileUrl;
        private String nickname;
        private String username;
        private Boolean isFriend;
    }

}
