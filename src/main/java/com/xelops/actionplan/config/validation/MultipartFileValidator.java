package com.xelops.actionplan.config.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.tika.metadata.HttpHeaders.CONTENT_TYPE;
import static org.apache.tika.metadata.TikaCoreProperties.RESOURCE_NAME_KEY;

@Slf4j
public class MultipartFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    private Set<MediaType> mediaTypes;
    private final Tika tika = new Tika();

    @Override
    public void initialize(ValidFile constraint) {
        log.info("initialize with constraint {}", constraint);
        this.mediaTypes = Stream.of(constraint.value())
                .map(mt -> mt.split("/"))
                .map(typeAndSubtype -> new MediaType(typeAndSubtype[0], typeAndSubtype[1]))
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        if (multipartFile == null) return true;
        var multipartFileContentType = tika.detect(multipartFile.getInputStream(), getMultipartFileMetadata(multipartFile));
        var multipartFileMediaType = MediaType.parseMediaType(multipartFileContentType);
        log.info("multipart validation - accepted: {} vs received {}", this.mediaTypes, multipartFileMediaType);
        return mediaTypes.stream().anyMatch(mediaType -> mediaType.isCompatibleWith(multipartFileMediaType));
    }

    private Metadata getMultipartFileMetadata(MultipartFile multipartFile) {
        var metadata = new Metadata();
        metadata.set(CONTENT_TYPE, multipartFile.getContentType());
        metadata.set(RESOURCE_NAME_KEY, multipartFile.getOriginalFilename());
        return metadata;
    }
}
