package com.umm.app.impl;

import com.umm.app.entity.User;
import com.umm.app.repository.UserRepository;
import com.umm.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    private CustomUserDetails createCustomUserDetails(User user) {
        return CustomUserDetails.of(user);
    }
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws RuntimeException{
        return userRepository.findByUsername(username).map(this::createCustomUserDetails).orElseThrow(()-> new BaseException(404, "존재하지 않는 유저입니다."));
    }
}
