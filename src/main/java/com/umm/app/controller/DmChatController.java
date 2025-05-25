package com.umm.app.controller;

import com.umm.app.dto.DmChatRecv;
import com.umm.app.dto.DmChatSend;
import com.umm.app.service.DmChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@MessageMapping("/dms")
public class DmChatController {

    private final DmChatService dmChatService;

    @MessageMapping("/{dmId}")
    @SendTo("/sub/dms/{dmId}")
    public DmChatSend sendChatMessage(SimpMessageHeaderAccessor accessor, @PathVariable String dmId, @Payload DmChatRecv dmChatRecv) {
        DmChatSend sendChat = dmChatService.recvDmChat(accessor, dmId, dmChatRecv);
        return sendChat;
    }
}
