package com.umm.app.service;

import com.umm.app.dto.PageableDmResponse;
import com.umm.app.dto.StartDmRequest;
import com.umm.app.dto.StartDmResponse;
import com.umm.app.entity.Dm;
import com.umm.app.entity.User;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.repository.DmRepository;
import com.umm.app.repository.UserRepository;
import com.umm.app.valid.CommonValidation;
import com.umm.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class DmService {

    private final DmRepository dmRepository;
    private final UserRepository userRepository;

    @Transactional
    public StartDmResponse createDm(CustomUserDetails customUserDetails, StartDmRequest startDmRequest) {

        CommonValidation.validateCustomUser(customUserDetails);

        User user = customUserDetails.getUser();
        User friend = userRepository.findByUsername(startDmRequest.getUsername()).orElseThrow(() -> new BaseException(404, "존재하지 않는 유저입니다."));

        validateDm(user, friend);

        UUID dmId = UUID.randomUUID();

        dmRepository.save(Dm.builder().dmId(dmId).user(user).friend(friend).isRead(false).build());
        dmRepository.save(Dm.builder().dmId(dmId).user(friend).friend(user).isRead(false).build());

        return StartDmResponse.builder().dmId(dmId).build();
    }

    @Transactional
    public PageableDmResponse listDms(CustomUserDetails customUserDetails) {

        CommonValidation.validateCustomUser(customUserDetails);

        User user = customUserDetails.getUser();
        List<Dm> dms = dmRepository.findAllByUser(user);

        return PageableDmResponse.builder()
                .dms(dms.stream().map(dm -> PageableDmResponse.Dms.builder()
                        .dmId(dm.getDmId())
                        .isRead(dm.getIsRead())
                        .profileUrl(dm.getFriend().getProfileUrl())
                        .username(dm.getFriend().getUsername())
                        .nickname(dm.getFriend().getNickname())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private void validateDm(User user, User friend) {
        if (dmRepository.findByUserAndFriend(user, friend).isPresent()){
            throw new BaseException(400, "이미 DM이 존재합니다.");
        };
    }
}
