package com.umm.app.controller;

import com.umm.app.dto.PageableDmChatResponse;
import com.umm.app.dto.PageableDmResponse;
import com.umm.app.dto.StartDmRequest;
import com.umm.app.dto.StartDmResponse;
import com.umm.app.impl.CustomUserDetails;
import com.umm.app.service.DmChatService;
import com.umm.app.service.DmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dms")
public class DmController {

    private final DmService dmService;
    private final DmChatService dmChatService;

    @PostMapping
    public ResponseEntity<StartDmResponse> createDm(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody StartDmRequest startDmRequest) {
        StartDmResponse response = dmService.createDm(customUserDetails, startDmRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageableDmResponse> listDms(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        PageableDmResponse response = dmService.listDms(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{dmId}")
    public ResponseEntity<PageableDmChatResponse> listChats(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID dmId, @RequestParam BigInteger key, @RequestParam Integer pageNumber) {
        PageableDmChatResponse response  = dmChatService.listDmChats(customUserDetails, dmId, key, pageNumber);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
