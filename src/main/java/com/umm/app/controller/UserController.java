package com.umm.app.controller;

import com.umm.app.dto.BaseResponse;
import com.umm.app.dto.SignInRequest;
import com.umm.app.dto.SignInResponse;
import com.umm.app.dto.SignUpRequest;
import com.umm.app.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signup(@RequestBody SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.builder().message("회원을 성공적으로 등록했습니다.").build());
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signin(@RequestBody SignInRequest signInRequest, HttpServletRequest request) {
        SignInResponse response = userService.signIn(signInRequest, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/exist")
    public String existUsername(@RequestParam String username) {
        return username;
    }

    @GetMapping("/profile")
    public String getProfile(@RequestParam String nickname) {
        return nickname;
    }

    @PutMapping("/profile")
    public String putProfile(String nickname) {
        return nickname;
    }

    @PostMapping("/add-friend")
    public String addFriend(String nickname) {
        return nickname;
    }

    @GetMapping("/friends")
    public String getFriends() {
        return "1";
    }

}
