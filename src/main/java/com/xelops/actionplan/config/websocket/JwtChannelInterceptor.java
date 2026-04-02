package com.xelops.actionplan.config.websocket;

import com.xelops.actionplan.utils.constants.GlobalConstants;
import com.xelops.actionplan.utils.security.AuthenticationFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationFactory authenticationFactory;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            Optional.of(accessor)
                    .map(a -> a.getFirstNativeHeader(HttpHeaders.AUTHORIZATION))
                    .map(a -> a.replace(GlobalConstants.HEADER_BEARER, Strings.EMPTY))
                    .ifPresent(token -> accessor.setUser(
                            authenticationManager.authenticate(authenticationFactory.getAuthentication(token)))
                    );
        }
        return message;
    }
}