package com.umm.app.service;

import com.umm.app.dto.*;
import com.umm.app.entity.Token;
import com.umm.app.entity.User;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.repository.TokenRepository;
import com.umm.app.repository.UserRepository;
import com.umm.app.util.ClientUtil;
import com.umm.app.auth.JwtProvider;
import com.umm.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        validateUser(signUpRequest);

        userRepository.save(User.builder()
                .username(signUpRequest.getUsername())
                .nickname(signUpRequest.getNickname())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .isActive(true)
                .email(null)
                .role(null)
                .profileUrl("")
                .phoneNumber(null)
                .build());
    }

    public SignInResponse signIn(SignInRequest signInRequest, HttpServletRequest request) {

        User user = userRepository.findByUsername(signInRequest.getUsername()).orElseThrow(() -> new BaseException(400, "존재하지 않는 유저이거나 틀린 비밀번호 입니다."));

        validateSignIn(signInRequest.getPassword(), user.getPassword());

        SignInResponse response = jwtProvider.generateToken(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        saveRefresh(user, response.getRefresh(), clientUtil.getClientIp(request));

        return response;
    }

    public void saveRefresh(User user, String refresh, String clientIp){
        // 1000ms * 60s * 60m * 72h
        Date refreshTokenExpiresIn = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 72);

        tokenRepository.save(
                Token.builder()
                        .user(user)
                        .refresh(refresh)
                        .expireAt(refreshTokenExpiresIn)
                        .lastLoginLocation(clientIp)
                        .isExpired(false)
                        .build());
    };

    public ExistUsernameReponse existUsername(ExistUsernameRequest existUsernameRequest) {
        boolean exist = false;
        String message = "사용 가능한 유저 이름입니다.";
        Optional<User> user = userRepository.findByUsername(existUsernameRequest.getUsername());

        if (user.isPresent()){
            exist = true;
            message = "이미 존재하는 유저 이름입니다.";
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

        validateCustomUser(customUserDetails);

        User user = customUserDetails.getUser();

        return ProfileResponse.builder().profileUrl(user.getProfileUrl()).nickname(user.getNickname()).build();
    }
    
    public RenewRefreshResponse renewRefresh(RenewRefreshRequest renewRefreshRequest, HttpServletRequest request) {

        Token token = tokenRepository.findByRefresh(renewRefreshRequest.getRefresh()).orElseThrow(() -> new BaseException(400, "잘못된 토큰입니다."));
        String clientIp = clientUtil.getClientIp(request);

        validateRefresh(token, clientIp);

        User user = token.getUser();

        Authentication authUser = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        SignInResponse response = jwtProvider.generateToken(authUser);

        saveRefresh(user, response.getRefresh(), clientIp);

        return RenewRefreshResponse.builder().access(response.getAccess()).refresh(response.getRefresh()).build();
    }

    private void validateUser(SignUpRequest signUpRequest){
        if (signUpRequest.getUsername().isBlank()){
            throw new BaseException(400, "유저 이름을 입력해주세요.");
        };
        if (signUpRequest.getUsername().contains(" ")){
            throw new BaseException(400, "유저 이름에 공백을 포함할 수 없습니다.");
        };
        if (signUpRequest.getUsername().length() < 2){
            throw new BaseException(400, "유저 이름은 최소 2글자 이상입니다.");
        };
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()){
            throw new BaseException(400, "이미 존재하는 유저 이름입니다.");
        };
    }
    private void validateCustomUser(CustomUserDetails customUserDetails){
        if (customUserDetails == null){
            throw new BaseException(401, "로그인이 필요한 정보입니다.");
        }
    }
    private void validateSignIn(String requestPassword, String userPassword){
        if (!passwordEncoder.matches(requestPassword, userPassword)){
            throw new BaseException(400, "존재하지 않는 유저이거나 틀린 비밀번호 입니다.");
        }
    }

    private void validateRefresh(Token token, String clientIp){

        if (!token.getLastLoginLocation().equals(clientIp)){
            throw new BaseException(401, "로그인 정보가 기존과 일치하지 않습니다.");
        }

        if (token.getIsExpired()){
            throw new BaseException(401, "만료된 리프레쉬 토큰입니다.");
        }

        Date now = new Date(System.currentTimeMillis());
        if (now.after(token.getExpireAt())){
            token.setIsExpired(true);
            throw new BaseException(401, "만료된 리프레쉬 토큰입니다.");
        }
    }
}
