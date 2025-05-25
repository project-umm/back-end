package com.umm.app.service;

import com.umm.app.dto.DmChatRecv;
import com.umm.app.dto.DmChatSend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class DmChatService {

    public DmChatSend recvDmChat(SimpMessageHeaderAccessor accessor, String dmId, DmChatRecv dmChatRecv) {
        return DmChatSend.builder().build();
    }
}
