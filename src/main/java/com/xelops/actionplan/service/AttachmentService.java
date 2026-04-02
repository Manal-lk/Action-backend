package com.xelops.actionplan.service;


import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Action;
import com.xelops.actionplan.domain.Attachment;
import com.xelops.actionplan.dto.AttachmentDataDto;
import com.xelops.actionplan.dto.AttachmentDto;
import com.xelops.actionplan.dto.UploadResult;
import com.xelops.actionplan.enumeration.StorageType;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.exception.StorageException;
import com.xelops.actionplan.mapper.AttachmentMapper;
import com.xelops.actionplan.repository.AttachmentRepository;
import com.xelops.actionplan.service.storage.StorageService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final StorageService storageService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final ActionHelperService actionHelperService;
    private final Messages messages;

    @Transactional
    public List<AttachmentDto> storeAttachments(Action action, MultipartFile[] files) throws StorageException {
        log.info("Start service storeAttachments | actionId: {} | numberOfFiles: {}", action.getId(), files.length);
        String dir = "actions/" + action.getId() + "/attachments";

        List<Attachment> attachments = Arrays.stream(files).map(file -> {
            String filename = file.getOriginalFilename();
            byte[] data;
            try {
                data = file.getBytes();
            } catch (IOException e) {
                throw new StorageException(StorageType.LOCAL_FS, messages.get(GlobalConstants.ATTACHMENT_FAILED_TO_READ_FILE_DATA_ERROR, filename));
            }
            UploadResult result = storageService.store(dir, filename, data);

            return Attachment.builder()
                    .action(action)
                    .storageType(result.storageType())
                    .name(filename)
                    .url(result.url())
                    .size(result.size())
                    .type(file.getContentType())
                    .build();
        }).collect(Collectors.toList());

        log.info("Completed service storeAttachments | actionId: {} | attachments size: {}", action.getId(), attachments.size());
        return attachmentRepository.saveAll(attachments)
                .stream().map(attachmentMapper::toAttachmentDto)
                .toList();
    }

    public List<AttachmentDto> getAttachmentsByActionId(Long boardId, Long actionId) throws NotFoundException {
        log.info("Start service getAttachmentsByActionId | boardId: {} | actionId: {}", boardId, actionId);
        
        actionHelperService.throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(boardId, actionId);
        
        List<Attachment> attachments = attachmentRepository.findByActionId(actionId);
        
        log.info("End service getAttachmentsByActionId | boardId: {} | actionId: {} | attachments size: {}",
                 boardId, actionId, attachments.size());
        return attachmentMapper.toAttachmentDtoList(attachments);
    }

    public AttachmentDataDto loadAttachment(Long boardId, Long actionId, Long attachmentId) throws NotFoundException {
        log.info("Start service loadAttachment | boardId: {} | actionId: {} | attachmentId: {}", boardId, actionId, attachmentId);
        actionHelperService.throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(boardId, actionId);

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ATTACHMENT_NOT_FOUND_ERROR, attachmentId)));
        String dir = "actions/" + actionId + "/attachments";
        InputStream inputStream = storageService.load(dir, attachment.getName());

        log.info("End service loadAttachment | boardId: {} | actionId: {} | attachmentId: {} | filename: {}",
                 boardId, actionId, attachmentId, attachment.getName());
        return AttachmentDataDto.builder()
                .filename(attachment.getName())
                .contentType(MediaType.valueOf(attachment.getType()))
                .size(attachment.getSize())
                .inputStream(inputStream)
                .build();
    }

    public void deleteAttachment(Long boardId, Long actionId, Long attachmentId) throws NotFoundException {
        log.info("Start service deleteAttachment | boardId: {} | actionId: {} | attachmentId: {}", boardId, actionId, attachmentId);
        actionHelperService.throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(boardId, actionId);

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ATTACHMENT_NOT_FOUND_ERROR, attachmentId)));
        String dir = "actions/" + actionId + "/attachments";
        storageService.delete(dir, attachment.getName());
        attachmentRepository.delete(attachment);

        log.info("End service deleteAttachment | boardId: {} | actionId: {} | attachmentId: {} | filename: {}",
                 boardId, actionId, attachmentId, attachment.getName());
    }
}
