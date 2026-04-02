package com.xelops.actionplan.dto;


import lombok.Builder;
import org.springframework.http.MediaType;

import java.io.InputStream;

@Builder
public record AttachmentDataDto(
        String filename,
        MediaType contentType,
        long size,
        InputStream inputStream
) {}
