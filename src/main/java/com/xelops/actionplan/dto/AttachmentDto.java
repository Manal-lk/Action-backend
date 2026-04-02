package com.xelops.actionplan.dto;

import lombok.Builder;

@Builder
public record AttachmentDto(
        Long id,
        String name,
        long size,
        String type,
        String storageType
) {}
