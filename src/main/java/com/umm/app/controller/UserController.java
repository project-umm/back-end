package com.umm.app.controller;

import com.umm.app.dto.BaseResponse;
import com.umm.app.dto.SignUpRequest;
import com.umm.app.service.UserService;

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
        return new ResponseEntity<>(BaseResponse.builder().message("성공").build(), HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public String signin() {
        return "index";
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
