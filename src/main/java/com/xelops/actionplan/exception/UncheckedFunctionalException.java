package com.xelops.actionplan.exception;

public class UncheckedFunctionalException extends RuntimeException {
    public UncheckedFunctionalException(FunctionalException ex) {
        super(ex.getMessage(), ex);
    }
}
