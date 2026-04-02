package com.xelops.actionplan.config;


import com.xelops.actionplan.enumeration.StorageType;
import com.xelops.actionplan.service.storage.FileSystemStorageService;
import com.xelops.actionplan.service.storage.OtherStorageService;
import com.xelops.actionplan.service.storage.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Value("${storage.type}")
    private StorageType storageType;

    private final Messages messages;

     public StorageConfig(Messages messages) {
        this.messages = messages;
    }


    @Bean
    public StorageService getStorageService() {
        if (storageType == StorageType.LOCAL_FS) {
            return new FileSystemStorageService(messages);
        } else {
            return new OtherStorageService();
        }
    }
}
