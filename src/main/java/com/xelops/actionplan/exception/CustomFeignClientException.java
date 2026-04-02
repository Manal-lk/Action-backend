package com.xelops.actionplan.exception;

import lombok.Getter;

@Getter
public class CustomFeignClientException extends RuntimeException {
    private final int status;
    private final String title;
    private final String msg;

    public CustomFeignClientException(String message, int status, String title, String msg) {
        super(message);
        this.status = status;
        this.title = title;
        this.msg = msg;
    }
}
