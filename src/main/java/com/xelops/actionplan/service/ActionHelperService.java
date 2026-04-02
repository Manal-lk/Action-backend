package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.repository.ActionRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionHelperService {

    private final ActionRepository actionRepository;
    private final Messages messages;
    private final UserHelperService userHelperService;


    /**
     * Throws if the action is not part of the specified board or if the user lacks access to the board.
     */
    public void throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(Long boardId, Long actionId) throws NotFoundException {
        log.info("Start service throwIfActionNotInBoard | boardId: {} | actionId: {}", boardId, actionId);
        if (!userHelperService.hasAccessToBoard(boardId)) {
            throw new NotFoundException(messages.get(GlobalConstants.USER_DOES_NOT_HAVE_ACCESS_TO_BOARD_ERROR, boardId));
        }

        if (!actionRepository.inBoard(actionId, boardId)) {
            throw new NotFoundException(messages.get(GlobalConstants.ACTION_NOT_FOUND_IN_BOARD_ERROR, actionId, boardId));
        }
        log.info("End service throwIfActionNotInBoard | boardId: {} | actionId: {} - without throwing", boardId, actionId);
    }
}
