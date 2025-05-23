package com.umm.app.service;

import com.umm.app.dto.*;
import com.umm.app.entity.User;
import com.umm.app.entity.UserAskFriend;
import com.umm.app.entity.UserFriend;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.repository.UserAskFriendRepository;
import com.umm.app.repository.UserFriendRepository;
import com.umm.app.repository.UserRepository;
import com.umm.app.valid.CommonValidation;
import com.umm.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class FriendService {

    private final UserRepository userRepository;
    private final UserFriendRepository userFriendRepository;
    private final UserAskFriendRepository userAskFriendRepository;

    public PageableUserResponse searchUsers(CustomUserDetails customUserDetails, String nickname){

        CommonValidation.validateCustomUser(customUserDetails);

        List<User> searchUsers = userRepository.findByNicknameContaining(nickname);
        List<UserFriend> friendUsers = userFriendRepository.findAllByUser(customUserDetails.getUser());
        List <User> filteredUser = filterUsers(customUserDetails.getUser(), searchUsers, friendUsers);

        return PageableUserResponse
                .builder()
                .users(filteredUser.stream().map(user -> PageableUserResponse.Users.builder()
                        .profileUrl(user.getProfileUrl())
                        .nickname(user.getNickname())
                        .username(user.getUsername())
                        .build()).toList())
                .build();
    }

    public PageableFriendResponse listFriends(CustomUserDetails customUserDetails){

        CommonValidation.validateCustomUser(customUserDetails);

        List<UserFriend> friends = userFriendRepository.findAllByUser(customUserDetails.getUser());

        // TODO : 로직 개선 필요(N+1)
        return PageableFriendResponse
                .builder()
                .friends(friends.stream().map(friend -> PageableFriendResponse.Friend.builder()
                        .profileUrl(friend.getFriend().getProfileUrl())
                        .nickname(friend.getFriend().getNickname())
                        .username(friend.getFriend().getUsername())
                        .build()).toList())
                .build();
    }
    public void askFriend(CustomUserDetails customUserDetails, AskRequest askRequest){

        CommonValidation.validateCustomUser(customUserDetails);

        // TODO : 로직 검증 고려 필요
        User user = customUserDetails.getUser();
        User friend = userRepository.findByUsername(askRequest.getUsername()).orElseThrow(() -> new BaseException(404, "존재하지 않는 유저입니다."));

        validateAskFriend(user, friend);

        userAskFriendRepository.save(UserAskFriend.builder().user(user).friend(friend).isPending(true).build());
    }

    public BaseResponse answerAsk(CustomUserDetails customUserDetails, AnswerRequest answerRequest){

        String message = "친구 요청을 거절하셨습니다.";

        if (answerRequest.getAnswer()){
            message = "친구 요청을 수락하셨습니다.";

            CommonValidation.validateUUID(answerRequest.getAskId());

            UserAskFriend ask = userAskFriendRepository.findById(UUID.fromString(answerRequest.getAskId())).orElseThrow(() -> new BaseException(404, "존재하지 않는 친구 요청입니다."));

            User friend = ask.getFriend();

            validateAnswerAsk(customUserDetails.getUser(), friend);

            User askUser = ask.getUser();

            userFriendRepository.save(UserFriend.builder().user(askUser).friend(friend).build());
            userFriendRepository.save(UserFriend.builder().user(friend).friend(askUser).build());

            // TODO : 역방향 친구 요청 조회 확인 및 조치 필요
            ask.setIsPending(false);
            userAskFriendRepository.save(ask);
        }

        return BaseResponse.builder().message(message).build();
    }

    public PageableAskUserResponse listAlarms(CustomUserDetails customUserDetails){

        CommonValidation.validateCustomUser(customUserDetails);

        User user = customUserDetails.getUser();

        // Friend 기준으로 친구 요청한 유저들을 조회
        List<UserAskFriend> asks = userAskFriendRepository.findAllByFriendAndIsPending(user, true);

        return PageableAskUserResponse.builder()
                .askUsers(asks.stream().map(
                        ask -> PageableAskUserResponse.askUser.builder()
                                .askId(ask.getId())
                                .nickname(ask.getUser().getNickname())
                                .username(ask.getUser().getUsername())
                                .profileUrl(ask.getUser().getProfileUrl())
                                .build())
                        .toList())
                .build();
    }


    private void validateAskFriend(User user, User friend){
        if (userAskFriendRepository.findByUserAndFriend(user, friend).isPresent()){
            throw new BaseException(400, "이미 요청된 사항입니다.");
        };
        if (user == friend){
            throw new BaseException(400, "스스로에게 요청할 수 없습니다.");
        }
    }

    private void validateAnswerAsk(User requestUser, User friend){
        if (!requestUser.getUsername().equals(friend.getUsername())){
            throw new BaseException(403, "수락할 권한이 없는 유저입니다.");
        }

    }

    private List<User> filterUsers(User user, List<User> searchUsers, List<UserFriend> friendUsers){

        List<User> filteredUsers = new ArrayList<>();
        HashMap<String,Boolean> friends = new HashMap<>();

        for (UserFriend friend: friendUsers){
            friends.put(friend.getFriend().getUsername(), true);
        };
        
        for(User searchUser:searchUsers){
            
            // 본인 제외
            if (searchUser.getUsername().equals(user.getUsername())){
                continue;
            }
            
            // 친구 제외
            if (friends.containsKey(searchUser.getUsername())){
                continue;
            }

            filteredUsers.add(searchUser);
        }

        return filteredUsers;
    }
}
