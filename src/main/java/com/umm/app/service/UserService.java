package com.umm.app.service;

import com.umm.app.dto.SignUpRequest;
import com.umm.app.entity.User;
import com.umm.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignUpRequest signUpRequest) {

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
}
