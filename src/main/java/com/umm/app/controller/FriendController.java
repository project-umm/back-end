package com.umm.app.controller;

import com.umm.app.dto.AskRequest;
import com.umm.app.dto.BaseResponse;
import com.umm.app.dto.PageableUserResponse;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/users")
    public ResponseEntity<PageableUserResponse> listUsers(@RequestParam String nickname) {
        PageableUserResponse response = friendService.listUsers(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/ask")
    public ResponseEntity<BaseResponse> askfriend(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody AskRequest askRequest) {
        friendService.askFriend(customUserDetails, askRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.builder().message("친구 요청에 성공하셨습니다.").build());
    }
}
