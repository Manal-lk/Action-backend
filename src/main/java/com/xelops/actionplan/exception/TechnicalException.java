package com.xelops.actionplan.exception;

import lombok.NoArgsConstructor;
@NoArgsConstructor
public class TechnicalException extends Exception {


    public TechnicalException(String message) {
        super(message);
    }
}
