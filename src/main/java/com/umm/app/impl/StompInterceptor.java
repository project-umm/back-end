package com.umm.app.impl;

import com.umm.app.auth.JwtProvider;
import com.umm.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        switch (Objects.requireNonNull(accessor.getCommand())) {
            case CONNECT -> log.info("CONNECT: " + message);
            case CONNECTED -> log.info("CONNECTED: " + message);
            case DISCONNECT -> log.info("DISCONNECT: " + message);
            case SUBSCRIBE -> log.info("SUBSCRIBE: " + message);
            case UNSUBSCRIBE -> log.info("UNSUBSCRIBE: " + sessionId);
            case SEND -> log.info("SEND: " + sessionId);
            case MESSAGE -> log.info("MESSAGE: " + sessionId);
            case ERROR -> log.info("ERROR: " + sessionId);
            default -> log.info("UNKNOWN: " + sessionId);
        }
    }

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        switch (Objects.requireNonNull(accessor.getCommand())) {
            case CONNECT -> {
                log.info("CONNECT: " + sessionId);
                handleAuthentication(accessor);
            }
            case SEND -> {
                log.info("SEND: " + sessionId);

                if (accessor.getUser() == null) {
                    handleAuthentication(accessor);
                }
            }
            case CONNECTED -> log.info("CONNECTED: " + sessionId);
            case DISCONNECT -> log.info("DISCONNECT: " + sessionId);
            // SUBSCRIBE 검증 필요
            case SUBSCRIBE -> log.info("SUBSCRIBE: " + sessionId);
            case UNSUBSCRIBE -> log.info("UNSUBSCRIBE: " + sessionId);
            case MESSAGE -> log.info("MESSAGE: " + sessionId);
            case ERROR -> log.info("ERROR: " + sessionId);
            default -> log.info("UNKNOWN: " + sessionId);
        }

        return message;
    }

    private void handleAuthentication(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization != null && !authorization.isEmpty()) {
            String token = authorization.substring(7);
            try {
                jwtProvider.validateToken(token);
                String username = jwtProvider.getUsernameFromToken(token);
                CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                accessor.setUser(authentication);
                Objects.requireNonNull(accessor.getSessionAttributes()).put("auth", username);

                log.info("웹소켓 인증 성공: {}", username);
                log.info(String.valueOf(accessor.getCommand()));

            } catch (Exception e) {
                log.error("웹소켓 인증 실패: {}", e.getMessage());
                throw new BaseException(400, "웹소켓 인증 실패");
            }
        } else {
            log.error("Authorization 헤더가 없습니다.");
            throw new BaseException(401, "인증 정보 없음");
        }
    }
}