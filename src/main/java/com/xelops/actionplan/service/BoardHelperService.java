package com.xelops.actionplan.service;


import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Board;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.repository.BoardColumnRepository;
import com.xelops.actionplan.repository.BoardRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BoardHelperService {

    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final Messages messages;

    public void hasAllColumnsOrThrow(Long boardId, Long fromColumnId, Long toColumnId) throws NotFoundException {
        log.info("Start service hasAllColumnsOrThrow | boardId: {}, fromColumnId: {}, toColumnId: {}", boardId, fromColumnId, toColumnId);
        List<Long> boardColumnIds = boardColumnRepository.findIdsByBoardId(boardId);
        if (!boardColumnIds.contains(fromColumnId) || !boardColumnIds.contains(toColumnId)) {
            throw new NotFoundException(messages.get(GlobalConstants.BOARD_COLUMNS_NOT_FOUND_IN_BOARD_ERROR, boardId));
        }
    }

    public Board getById(Long boardId) throws NotFoundException {
        log.info("Start service get Board by id: {}", boardId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.BOARD_NOT_FOUND_ERROR, boardId)));
        log.info("End service get Board by id: {} | name: {}", boardId, board.getName());
        return board;
    }
}
