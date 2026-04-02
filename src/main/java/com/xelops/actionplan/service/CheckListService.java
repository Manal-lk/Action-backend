package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Action;
import com.xelops.actionplan.domain.CheckList;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.CheckListDto;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.CheckListMapper;
import com.xelops.actionplan.repository.ActionRepository;
import com.xelops.actionplan.repository.CheckListRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckListService {

    private final CheckListRepository checkListRepository;
    private final CheckListMapper checkListMapper;
    private final UserHelperService userHelperService;
    private final ActionRepository actionRepository;
    private final Messages messages;

    @Transactional
    public List<CheckListDto> createCheckListsForAction(Long actionId, List<CheckListDto> checkListDtos) throws NotFoundException {
        log.info("Start service createCheckListsForAction | actionId: {}", actionId);

        // Verify organization access to action (defense in depth)
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ACTION_NOT_FOUND_ERROR, actionId)));
        userHelperService.verifyOrganizationAccess(action.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());

        List<CheckList> existingCheckLists = checkListRepository.findByActionId(actionId);
        if (!existingCheckLists.isEmpty()) {
            log.info("Deleting {} existing checklists for action {}", existingCheckLists.size(), actionId);
            checkListRepository.deleteByActionId(actionId);
        }
        User user = userHelperService.getConnectedUser();
        List<CheckList> checkList = checkListMapper.toCheckList(checkListDtos, user, actionId);
        List<CheckList> savedCheckLists = checkListRepository.saveAll(checkList);
        List<CheckListDto> savedCheckListDtos = checkListMapper.toCheckListDtoList(savedCheckLists);

        log.info("End service createCheckListsForAction | created {} checklists", savedCheckListDtos.size());
        return savedCheckListDtos;
    }
}
