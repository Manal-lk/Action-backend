package com.xelops.actionplan.dto.comment;

import com.xelops.actionplan.dto.UserSimplifiedDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String message;
    private UserSimplifiedDto user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

