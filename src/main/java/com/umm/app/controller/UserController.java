package com.umm.app.controller;

import com.umm.app.dto.*;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.builder().message("회원을 성공적으로 등록했습니다.").build());
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest signInRequest, HttpServletRequest request) {
        SignInResponse response = userService.signIn(signInRequest, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/exist")
    public ResponseEntity<ExistUsernameReponse> existUsername(@RequestParam ExistUsernameRequest username) {
        ExistUsernameReponse response = userService.existUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/my-profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ProfileResponse response = userService.getMyProfile(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> putProfile(@RequestParam String nickname) {
        ProfileResponse response = userService.getProfile(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<RenewRefreshResponse> renewRefresh(@RequestBody RenewRefreshRequest renewRefreshRequest, HttpServletRequest request) {
        RenewRefreshResponse response = userService.renewRefresh(renewRefreshRequest, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
