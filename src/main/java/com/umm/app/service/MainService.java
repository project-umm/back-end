package com.umm.app.service;

import com.umm.app.dto.MainDashboardResponse;
import com.umm.app.dto.ProfileResponse;
import com.umm.app.entity.User;
import com.umm.app.entity.UserAskFriend;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.repository.UserAskFriendRepository;
import com.umm.app.repository.UserRepository;
import com.umm.app.valid.CommonValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
@Slf4j
public class MainService {

    private final UserAskFriendRepository userAskFriendRepository;

    public MainDashboardResponse getDashboard(CustomUserDetails customUserDetails){

        CommonValidation.validateCustomUser(customUserDetails);

        User user = customUserDetails.getUser();

        Integer alarm_number = userAskFriendRepository.findAllByFriendAndIsPending(user, true).size();

        // TODO : Dm List
        return MainDashboardResponse.builder()
                .alertNumber(alarm_number)
                .dmUsers(new ArrayList<>())
                .myProfile(ProfileResponse.builder()
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .profileUrl(user.getProfileUrl())
                        .build())
                .build();
    }
}
