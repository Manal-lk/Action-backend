package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.BoardColumn;
import com.xelops.actionplan.dto.BoardColumnSimplifiedDto;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.BoardColumnMapper;
import com.xelops.actionplan.repository.BoardColumnRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class BoardColumnService {

    private final BoardColumnRepository boardColumnRepository;
    private final BoardColumnMapper boardColumnMapper;
    private final UserHelperService userHelperService;
    private final Messages messages;

    public boolean exists(Long boardColumnId) {
        log.info("Start service exists | id: {}", boardColumnId);
        boolean exists = boardColumnRepository.existsById(boardColumnId);
        log.info("End service exists | id: {} | name: {}", boardColumnId, exists);
        return exists;
    }

    @Transactional
    public void moveColumn(Long boardId, Long columnId, int insertAtIndex) throws NotFoundException {
        log.info("Start service moveColumn | boardId: {} | columnId: {}", boardId, columnId);

        // SECURITY CHECK FIRST: Fetch and verify organization access immediately
        BoardColumn movedColumn = boardColumnRepository.findByIdAndBoardId(columnId, boardId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.COLUMN_NOT_FOUND_IN_BOARD_ERROR, columnId, boardId)));
        userHelperService.verifyOrganizationAccess(movedColumn.getBoard().getWorkspace().getOrganization().getId());

        if (!userHelperService.hasAccessToBoard(boardId)) {
            throw new NotFoundException(messages.get(GlobalConstants.USER_DOES_NOT_HAVE_ACCESS_TO_BOARD_ERROR, boardId));
        }

        List<BoardColumn> columns = new ArrayList<>(
                boardColumnRepository.findAllByBoardIdOrderByOffset(boardId).stream().filter(c -> !Objects.equals(c.getId(), columnId)).toList()
        );
        columns.add(insertAtIndex, movedColumn);
        for (int i = 0; i < columns.size(); i++) {
            BoardColumn c = columns.get(i);
            c.setOffset(i);
        }
        boardColumnRepository.saveAll(columns);

        log.info("End service moveColumn | boardId: {} | columnId: {}", boardId, columnId);
    }

    public List<BoardColumnSimplifiedDto> getSimplifiedBoardColumnsByBoardId(Long boardId) {
        log.info("Start service getSimplifiedBoardColumnsByBoardId | boardId: {}", boardId);
        List<BoardColumn> boardColumns = boardColumnRepository.findAllByBoardIdOrderByOffset(boardId);
        List<BoardColumnSimplifiedDto> simplifiedDtos = boardColumnMapper.toBoardColumnSimplifiedDtos(boardColumns);
        log.info("End service getSimplifiedBoardColumnsByBoardId | boardId: {} | size: {}", boardId, simplifiedDtos.size());
        return simplifiedDtos;
    }

    public List<BoardColumn> findAllByBoardIdOrderByOffset(Long boardId) {
        log.info("Start service findAllByBoardIdOrderByOffset | boardId: {}", boardId);
        List<BoardColumn> boardColumns = boardColumnRepository.findAllByBoardIdOrderByOffset(boardId);
        log.info("End service findAllByBoardIdOrderByOffset | boardId: {} | size: {}", boardId, boardColumns.size());
        return boardColumns;
    }

    public BoardColumn findById(Long id) {
        log.info("Start service findById | id: {}", id);
        BoardColumn boardColumn = boardColumnRepository.findById(id).orElse(null);
        log.info("End service findById | id: {} | found: {}", id, boardColumn != null);
        return boardColumn;
    }
}
