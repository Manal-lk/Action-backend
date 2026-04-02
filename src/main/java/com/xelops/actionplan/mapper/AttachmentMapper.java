package com.xelops.actionplan.mapper;


import com.xelops.actionplan.domain.Attachment;
import com.xelops.actionplan.dto.AttachmentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    AttachmentDto toAttachmentDto(Attachment attachment);
    List<AttachmentDto> toAttachmentDtoList(List<Attachment> attachments);
}
