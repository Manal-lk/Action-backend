package com.xelops.actionplan.service.storage;

import com.xelops.actionplan.dto.UploadResult;
import com.xelops.actionplan.enumeration.StorageType;
import com.xelops.actionplan.exception.StorageException;
import jakarta.validation.constraints.NotEmpty;

import java.io.InputStream;

public interface StorageService {

    StorageType getStorageType();
    boolean exists(String path);
    UploadResult store(@NotEmpty String dir, @NotEmpty String filename, byte[] data) throws StorageException;
    InputStream load(String dir, String filename);
    void delete(String dir, String filename);
}