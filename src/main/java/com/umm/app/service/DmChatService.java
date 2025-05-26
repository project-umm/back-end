package com.umm.app.service;

import com.umm.app.dto.DmChatRecv;
import com.umm.app.dto.DmChatSend;
import com.umm.app.dto.PageableDmChatResponse;
import com.umm.app.entity.Dm;
import com.umm.app.entity.DmChat;
import com.umm.app.entity.User;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.repository.DmChatRepository;
import com.umm.app.repository.DmRepository;
import com.umm.app.repository.UserRepository;
import com.umm.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class DmChatService {

    private final UserRepository userRepository;
    private final DmRepository dmRepository;
    private final DmChatRepository dmChatRepository;

    @Transactional
    public DmChatSend recvDmChat(SimpMessageHeaderAccessor accessor, String dmId, DmChatRecv dmChatRecv) {

        String username = accessor.getSessionAttributes().get("auth").toString();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BaseException(404, "존재하지 않는 유저입니다."));

        DmChat dmChat = dmChatRepository.save(DmChat.builder().dmId(UUID.fromString(dmId)).user(user).chatMessage(dmChatRecv.getChatMessage()).build());

        return DmChatSend.builder()
                .chatMessage(dmChat.getChatMessage())
                .profileUrl(user.getProfileUrl())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .createdAt(dmChat.getCreatedAt().getTime()).build();
    }

    @Transactional
    public PageableDmChatResponse listDmChats(CustomUserDetails customUserDetails, UUID dmId, BigInteger key, Integer pageNumber){
        // TODO : DM Validate
        User user = customUserDetails.getUser();

        if (key == null || key.equals(BigInteger.ZERO)){
            Long count = dmChatRepository.count();
            key = BigInteger.valueOf(count);
        }

        List<DmChat> dmChats= dmChatRepository.findDmChatNextPage(dmId, key, Pageable.ofSize(pageNumber+1));

        return PageableDmChatResponse.builder()
                .chats(dmChats.stream().map(
                        dmChat -> PageableDmChatResponse.Chats.builder()
                                .createdAt(dmChat.getCreatedAt().getTime())
                                .chatMessage(dmChat.getChatMessage())
                                .username(dmChat.getUser().getUsername())
                                .nickname(dmChat.getUser().getNickname())
                                .profileUrl(dmChat.getUser().getProfileUrl())
                                .build()
                ).toList())
                .isLast(dmChats.size() != pageNumber+1)
                .key(dmChats.getLast().getKey())
                .objectNumber(dmChats.size() == pageNumber+1 ? pageNumber : dmChats.size())
                .build();
    }
}
