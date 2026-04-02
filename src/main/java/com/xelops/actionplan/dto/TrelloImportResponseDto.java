package com.xelops.actionplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrelloImportResponseDto {

    private Long boardId;
    private String boardName;
    private Integer columnsCreated;
    private Integer actionsCreated;
    private Integer membersAdded;
}
