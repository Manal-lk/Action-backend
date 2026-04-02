package com.xelops.actionplan.service.storage;


import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.dto.UploadResult;
import com.xelops.actionplan.enumeration.StorageType;
import com.xelops.actionplan.exception.StorageException;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
public class FileSystemStorageService implements StorageService {

    @Value("${storage.local.root-dir}")
    private String rootDir;

    private final Messages messages;

    @Override
    public StorageType getStorageType() {
        return StorageType.LOCAL_FS;
    }

    @Override
    public boolean exists(String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            return false;
        }
        return Files.exists(Paths.get(path));
    }
    @Override
    public UploadResult store(@NotEmpty String dir, @NotNull String filename, byte[] data) throws StorageException {
        Path dirPath = Paths.get(rootDir, dir);
        if (!exists(dirPath.toString())) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                log.error("Failed to create directory: {}", dir, e);
                throw new StorageException(getStorageType(), messages.get(GlobalConstants.STORAGE_FAILED_TO_CREATE_DIRECTORY_ERROR));
            }
        }

        try {

            InputStream inputStream = new java.io.ByteArrayInputStream(data);
            // Copy file to the target location (Replacing existing file with same name)
            Path targetLocation = dirPath.resolve(filename);
            long size = Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return UploadResult.builder()
                    .storageType(StorageType.LOCAL_FS)
                    .url(targetLocation.toFile().getAbsolutePath())
                    .size(size)
                    .build();
        } catch (IOException ex) {
            throw new StorageException(getStorageType(), messages.get(GlobalConstants.STORAGE_FAILED_TO_STORE_FILE_ERROR, filename));
        }
    }

    @Override
    public InputStream load(String dir, String filename) {
        Path filePath = Paths.get(rootDir, dir, filename);
        if (!exists(filePath.toString())) {
            throw new StorageException(getStorageType(), messages.get(GlobalConstants.STORAGE_FILE_NOT_FOUND_ERROR, filename));
        }
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            log.error("Failed to read file: {}", filename, e);
            throw new StorageException(getStorageType(), messages.get(GlobalConstants.STORAGE_FAILED_TO_READ_FILE_ERROR, filename));
        }
    }

    @Override
    public void delete(String dir, String filename) {
        Path filePath = Paths.get(rootDir, dir, filename);
        if (!exists(filePath.toString())) {
            throw new StorageException(getStorageType(), messages.get(GlobalConstants.STORAGE_FILE_NOT_FOUND_ERROR, filename));
        }
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filename, e);
            throw new StorageException(getStorageType(), messages.get(GlobalConstants.STORAGE_FAILED_TO_DELETE_FILE_ERROR, filename));
        }
    }
}
