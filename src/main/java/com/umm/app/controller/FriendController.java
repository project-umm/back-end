package com.umm.app.controller;

import com.umm.app.dto.*;
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

    @GetMapping
    public ResponseEntity<PageableFriendResponse> listFriends(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        PageableFriendResponse response = friendService.listFriends(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/users")
    public ResponseEntity<PageableUserResponse> searchUsers(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam String nickname) {
        PageableUserResponse response = friendService.searchUsers(customUserDetails, nickname);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/ask")
    public ResponseEntity<BaseResponse> askFriend(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody AskRequest askRequest) {
        friendService.askFriend(customUserDetails, askRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.builder().message("친구 요청에 성공하셨습니다.").build());
    }

    @PostMapping("/answer")
    public ResponseEntity<BaseResponse> answerAsk(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody AnswerRequest answerRequest) {
        BaseResponse response = friendService.answerAsk(customUserDetails, answerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/alarms")
    public ResponseEntity<PageableAskUserResponse> listAlarms(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        PageableAskUserResponse response = friendService.listAlarms(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
