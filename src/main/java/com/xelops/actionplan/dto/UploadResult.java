package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.StorageType;
import lombok.Builder;

@Builder
public record UploadResult(

        StorageType storageType,
        String url,
        String filename,
        long size
) {}
