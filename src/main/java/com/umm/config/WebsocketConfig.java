package com.umm.config;

import com.umm.app.impl.StompInterceptor;
import com.umm.exception.StompErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompInterceptor stompInterceptor;
    private final StompErrorHandler stompErrorHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").addInterceptors().setAllowedOrigins("*");
        registry.setErrorHandler(stompErrorHandler);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }
}
