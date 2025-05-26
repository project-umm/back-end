package com.umm.app.service;

import com.umm.app.dto.MainDashboardResponse;
import com.umm.app.dto.PageableDmResponse;
import com.umm.app.dto.ProfileResponse;
import com.umm.app.entity.Dm;
import com.umm.app.entity.User;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.repository.DmRepository;
import com.umm.app.repository.UserAskFriendRepository;
import com.umm.app.valid.CommonValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class MainService {

    private final UserAskFriendRepository userAskFriendRepository;
    private final DmRepository dmRepository;

    public MainDashboardResponse getDashboard(CustomUserDetails customUserDetails){

        CommonValidation.validateCustomUser(customUserDetails);

        User user = customUserDetails.getUser();

        Integer alarm_number = userAskFriendRepository.findAllByFriendAndIsPending(user, true).size();
        List<Dm> dms = dmRepository.findAllByUser(user);

        return MainDashboardResponse.builder()
                .alertNumber(alarm_number)
                .dmUsers(dms.stream().map(dm -> PageableDmResponse.Dms.builder()
                        .dmId(dm.getDmId())
                        .isRead(dm.getIsRead())
                        .profileUrl(dm.getFriend().getProfileUrl())
                        .username(dm.getFriend().getUsername())
                        .nickname(dm.getFriend().getNickname())
                        .build()).collect(Collectors.toList()))
                .myProfile(ProfileResponse.builder()
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .profileUrl(user.getProfileUrl())
                        .build())
                .build();
    }
}
