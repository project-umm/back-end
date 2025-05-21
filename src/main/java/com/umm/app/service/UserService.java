package com.umm.app.service;

import com.umm.app.dto.*;
import com.umm.app.entity.Token;
import com.umm.app.entity.User;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.repository.TokenRepository;
import com.umm.app.repository.UserRepository;
import com.umm.app.util.ClientUtil;
import com.umm.app.util.JwtProvider;
import com.umm.exception.BaseException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final ClientUtil clientUtil;

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

    public SignInResponse signIn(SignInRequest signInRequest, HttpServletRequest request) {

        User user = userRepository.findByUsername(signInRequest.getUsername()).orElseThrow(() -> new BaseException(400, "존재하지 않는 유저이거나 틀린 비밀번호 입니다."));

        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())){
            throw new BaseException(400, "존재하지 않는 유저이거나 틀린 비밀번호 입니다.");
        }

        Authentication authUser = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        SignInResponse response = jwtProvider.generateToken(authUser);

        saveRefresh(user, response.getRefresh(), clientUtil.getClientIp(request));

        return response;
    }

    public void saveRefresh(User user, String refresh, String clientIp){

        validRefresh();

        long now = System.currentTimeMillis();
        Date refreshTokenExpiresIn = new Date(now + 1800000); // 30분

        Token token = tokenRepository.save(
                Token.builder()
                        .user(user)
                        .refresh(refresh)
                        .expireAt(refreshTokenExpiresIn)
                        .lastLoginLocation(clientIp)
                        .isExpired(false)
                        .build());
        return;
    };

    public void validRefresh(){
        return;
    }

    public ExistUsernameReponse existUsername(ExistUsernameRequest existUsernameRequest) {
        boolean exist = false;
        String message = "사용 가능한 이름입니다.";
        Optional<User> user = userRepository.findByUsername(existUsernameRequest.getUsername());

        if (user.isPresent()){
            exist = true;
            message = "이미 존재하는 이름입니다.";
        }

        return ExistUsernameReponse.builder().exist(exist).message(message).build();

    }

    public PageableUserResponse listUsers(@RequestParam String nickname){
        // TODO 친구 여부 확인
        List<User> users = userRepository.findByNicknameContaining(nickname);

        return PageableUserResponse
                .builder()
                .users(users.stream().map(user -> PageableUserResponse.Users.builder()
                    .profileUrl(user.getProfileUrl())
                    .nickname(user.getNickname())
                    .username(user.getUsername())
                    .build()).toList())
                .build();
    }

    public ProfileResponse getMyProfile(CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        return ProfileResponse.builder().profileUrl(user.getProfileUrl()).build();
    }
}
