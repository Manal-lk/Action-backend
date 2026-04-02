package com.xelops.actionplan.service.storage;

import com.xelops.actionplan.dto.UploadResult;
import com.xelops.actionplan.enumeration.StorageType;
import com.xelops.actionplan.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
public class OtherStorageService implements StorageService {

    @Override
    public StorageType getStorageType() {
        return StorageType.OTHER;
    }

    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    public UploadResult store(String dir, String filename, byte[] data) throws StorageException {
        return null;
    }

    @Override
    public InputStream load(String dir, String filename) {
        return new InputStream() {
            @Override
            public int read() {
                return -1; // End of stream
            }
        };
    }

    @Override
    public void delete(String dir, String filename) {
        // Not implemented
    }
}
