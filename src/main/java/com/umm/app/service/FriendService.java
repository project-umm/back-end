package com.umm.app.service;

import com.umm.app.dto.AskRequest;
import com.umm.app.dto.PageableUserResponse;
import com.umm.app.entity.User;
import com.umm.app.entity.UserAskFriend;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.repository.UserAskFriendRepository;
import com.umm.app.repository.UserRepository;
import com.umm.app.valid.CommonValidation;
import com.umm.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class FriendService {

    private final UserRepository userRepository;
    private final UserAskFriendRepository userAskFriendRepository;

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

    public void askFriend(CustomUserDetails customUserDetails, AskRequest askRequest){

        // TODO : 검증
        CommonValidation.validateCustomUser(customUserDetails);

        User user = customUserDetails.getUser();
        User friend = userRepository.findByUsername(askRequest.getUsername()).orElseThrow(() -> new BaseException(404, "존재하지 않는 유저입니다."));

        validateAskFriend(user, friend);

        userAskFriendRepository.save(UserAskFriend.builder().user(user).friend(friend).isPending(true).build());
    }

    private void validateAskFriend(User user, User friend){
        if (userAskFriendRepository.findByUserAndFriend(user, friend).isPresent()){
            throw new BaseException(400, "이미 요청된 사항입니다.");
        };
    }
}
