package com.xelops.actionplan.exception;

import com.xelops.actionplan.enumeration.StorageType;

public class StorageException extends RuntimeException {
    public StorageException(StorageType storageType, String message) {
        super(storageType.getLabel() + " - " + message);
    }
}