package com.project.cozystay.config;


import com.project.cozystay.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith(BEARER_PREFIX)) {
                token = token.substring(BEARER_PREFIX.length());

                if (jwtProvider.validateToken(token)) {
                    Long userId = jwtProvider.getUserId(token);
                    String roleKey = jwtProvider.getRole(token);
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            userId, // Principal로 사용될 값 (컨트롤러에서 추출)
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(roleKey))
                    );
                    accessor.setUser(auth);
                    log.info("WebSocket 인증 성공 - UserId: {}", userId);
                }
            }
        }
        return message;
    }
}
