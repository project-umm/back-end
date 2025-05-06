package com.umm.app.service;

import com.umm.app.dto.SignUpRequest;
import com.umm.app.entity.User;
import com.umm.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    private void signUp(SignUpRequest signUpRequest) {

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .nickname(signUpRequest.getNickname())
//                .password()
                .build();



        userRepository.save(user);
    }
}
