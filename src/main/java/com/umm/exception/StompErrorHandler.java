package com.umm.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umm.app.dto.ErrorResponse;
import com.umm.app.dto.StompErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        if (ex instanceof MessageDeliveryException) {
            Throwable cause = ex.getCause();
            if (cause instanceof AccessDeniedException) {
                return sendErrorMessage(StompErrorResponse.builder().message("웹소켓 에러가 발생했습니다.").customMessage(cause.getMessage()).statusCode(500).build());
            }

            if (cause instanceof AuthException) {
                return sendErrorMessage(StompErrorResponse.builder().message("사용자 웹소켓 에러가 발생했습니다.").customMessage(cause.getMessage()).statusCode(401).build());
            }

            if (cause instanceof BaseException) {
                return sendErrorMessage(StompErrorResponse.builder().message("사용자 웹소켓 에러가 발생했습니다.").customMessage(cause.getMessage()).statusCode(400).build());
            }

            if (cause instanceof Exception) {
                return sendErrorMessage(StompErrorResponse.builder().message("서버에서 웹소켓 에러가 발생했습니다.").customMessage(cause.getMessage()).statusCode(500).build());
            }

        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> sendErrorMessage(StompErrorResponse stompErrorResponse) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.ERROR);
        headers.setMessage(stompErrorResponse.getMessage());
        headers.setLeaveMutable(true);

        try {
            String json = objectMapper.writeValueAsString(stompErrorResponse);
            return MessageBuilder.createMessage(json.getBytes(StandardCharsets.UTF_8),
                    headers.getMessageHeaders());
        } catch (JsonProcessingException e) {
            log.error("Failed to convert ErrorResponse to JSON", e);
            return MessageBuilder.createMessage(stompErrorResponse.getMessage().getBytes(StandardCharsets.UTF_8),
                    headers.getMessageHeaders());
        }
    }
}