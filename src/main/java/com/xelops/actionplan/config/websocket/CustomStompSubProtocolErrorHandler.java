package com.xelops.actionplan.config.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xelops.actionplan.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
class CustomStompSubProtocolErrorHandler extends StompSubProtocolErrorHandler {

        private Message<byte[]> handleException(final Message<byte[]> clientMessage, final Throwable ex, HttpStatus httpStatus)
        {
            final var message = ex.getMessage();
            final var errCode = String.valueOf(httpStatus.value());
            final var apiError = ApiError.builder()
                    .errCode(errCode)
                    .message(message)
                    .build();

            return prepareErrorMessage(clientMessage, apiError, errCode);
        }

        private Message<byte[]> prepareErrorMessage(final Message<byte[]> clientMessage, ApiError apiError, String errorCode)
        {
            String message = null;
            try {
                message = new ObjectMapper().writeValueAsString(apiError);
            } catch (JsonProcessingException e) {
                errorCode = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
                apiError.setErrCode(errorCode);
            }

            final var accessor = StompHeaderAccessor.create(StompCommand.ERROR);

            accessor.setReceipt(String.valueOf(clientMessage.hashCode()));
            accessor.setMessage(errorCode);
            accessor.setLeaveMutable(true);

            return MessageBuilder.createMessage(message != null ? message.getBytes() : new byte[]{}, accessor.getMessageHeaders());
        }

        @Override
        public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
            if (ex.getCause() instanceof AccessDeniedException accessDeniedException) {
                return handleException(clientMessage, accessDeniedException, HttpStatus.UNAUTHORIZED);
            }

            return super.handleClientMessageProcessingError(clientMessage, ex);
        }
    }