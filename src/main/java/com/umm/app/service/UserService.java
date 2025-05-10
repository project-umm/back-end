package com.umm.app.service;

import com.umm.app.dto.SignInRequest;
import com.umm.app.dto.SignUpRequest;
import com.umm.app.entity.User;
import com.umm.app.repository.UserRepository;
import com.umm.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public void signUp(SignUpRequest signUpRequest) {

        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()){
            throw new BaseException(400, "이미 존재하는 유저입니다.");
        };

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .nickname(signUpRequest.getNickname())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .isActive(true)
                .email(null)
                .role(null)
                .profileUrl(null)
                .phoneNumber(null)
                .build();

        userRepository.save(user);
    }

    public void signIn(SignInRequest signInRequest) {

        User user = userRepository.findByUsername(signInRequest.getUsername()).orElseThrow(() -> new BaseException(404, "존재하지 않는 유저입니다."));

        Authentication authUser = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        System.out.println(authUser.toString());
        System.out.println(user.getPassword());
        System.out.println(passwordEncoder.encode(signInRequest.getPassword()));
        System.out.println(passwordEncoder.matches(signInRequest.getPassword(), user.getPassword()));

    }
}
