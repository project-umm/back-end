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
    private ResponseEntity<BaseResponse> signup(@RequestBody SignUpRequest signUpRequest) {

        return new ResponseEntity<>(BaseResponse.builder().message("성공").build(), HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    private String signin() {
        return "index";
    }

    @GetMapping("/exist")
    private String existUsername(@RequestParam String username) {
        return username;
    }

    @GetMapping("/profile")
    private String getProfile(@RequestParam String nickname) {
        return nickname;
    }

    @PutMapping("/profile")
    private String putProfile(String nickname) {
        return nickname;
    }

    @PostMapping("/add-friend")
    private String addFriend(String nickname) {
        return nickname;
    }

    @GetMapping("/friends")
    private String getFriends() {
        return "1";
    }

}
