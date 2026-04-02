package com.xelops.actionplan.service;

import com.xelops.actionplan.domain.*;
import com.xelops.actionplan.dto.*;
import com.xelops.actionplan.enumeration.ActionStatusEnum;
import com.xelops.actionplan.enumeration.CustomFieldType;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrelloImportService {

    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final ActionRepository actionRepository;
    private final ActionMemberRepository actionMemberRepository;
    private final UserBoardRepository userBoardRepository;
    private final UserRepository userRepository;
    private final UserHelperService userHelperService;
    private final CustomFieldService customFieldService;
    private final BoardService boardService;
    private final CustomFieldRepository customFieldRepository;
    private final CustomFieldOptionRepository customFieldOptionRepository;

    @Transactional
    public TrelloImportResponseDto importBoard(Long workspaceId, TrelloImportRequestDto trelloData) throws FunctionalException, NotFoundException {
        log.info("Start service importBoard | name: {} | workspaceId: {}", trelloData.getName(), workspaceId);

        // 1. Get connected user and validate workspace access
        final var userId = userHelperService.getConnectedUserDetails().userId();
        userHelperService.validateUserInWorkspace(userId, workspaceId);

        // 2. Check for duplicate board name
        boardService.checkDuplicateBoardName(trelloData.getName(), workspaceId);

        // 3. Process custom fields and options (before creating actions) -- for now the custom options are global and not specific to a board
        if (trelloData.getCustomFields() != null && !trelloData.getCustomFields().isEmpty()) {
            log.info("Processing custom fields before board import");
            processCustomFieldDefinitions(trelloData.getCustomFields());
        }

        // 4. Create Board
        Board board = createBoard(workspaceId, trelloData);

        // 5. Add board members
        int membersAdded = addBoardMembers(board, trelloData.getMembers(), userId);

        // 6. Create columns with actions
        List<BoardColumn> columns = boardColumnRepository.saveAll(
          trelloData.getColumns().stream().map(columnDto -> BoardColumn.builder()
                  .name(columnDto.getName())
                  .offset(columnDto.getOffset())
                  .board(board)
                  .build()
          ).collect(Collectors.toList())
        );

        Map<String, BoardColumn> columnsMappedByName = columns.stream()
                .collect(Collectors.toMap(BoardColumn::getName, column -> column));

        List<ActionMember> actionMembers = new ArrayList<>();

        int actionsCreated = 0;
        for (TrelloColumnDto column: trelloData.getColumns()) {
            for (TrelloActionDto actionDto: column.getActions()) {
                Action action = createActionAndSave(columnsMappedByName.get(column.getName()), actionDto);
                actionsCreated++;
                if (actionDto.getMembers() != null && !actionDto.getMembers().isEmpty()) {
                    actionMembers.addAll(createActionMembers(action, actionDto.getMembers()));
                }
            }
        }

        actionMemberRepository.saveAll(actionMembers);

        log.info("End service importBoard | boardId: {} | columns: {} | actions: {} | members: {}",
                board.getId(), columns.size(), actionsCreated, membersAdded);

        return TrelloImportResponseDto.builder()
                .boardId(board.getId())
                .boardName(board.getName())
                .columnsCreated(columns.size())
                .actionsCreated(actionsCreated)
                .membersAdded(membersAdded)
                .build();
    }

    private Board createBoard(Long workspaceId, TrelloImportRequestDto trelloData) {
        Board board = Board.builder()
                .name(trelloData.getName())
                .description(trelloData.getDescription())
                .workspace(Workspace.builder().id(workspaceId).build())
                .active(true)
                .build();

        return boardRepository.save(board);
    }

    private int addBoardMembers(Board board, List<TrelloMemberDto> members, Long creatorUserId) {
        if (members == null || members.isEmpty()) {
            // At minimum, add the creator as administrator
            User creator = userRepository.findById(creatorUserId)
                    .orElse(null);
            userBoardRepository.save(
                    UserBoard.builder()
                            .board(board)
                            .user(creator)
                            .profile(UserProfileEnum.ADMINISTRATOR)
                            .starred(false)
                            .build()
            );
            return 1;
        }

        Set<Long> uniqueUserIds = new HashSet<>();
        uniqueUserIds.add(creatorUserId); // Always add creator

        for (var member : members) {
            if (member.getId() != null) {
                uniqueUserIds.add(member.getId());
            }
        }

        List<UserBoard> userBoardList = uniqueUserIds.stream()
                .map(memberId -> {
                    UserProfileEnum profile = memberId.equals(creatorUserId)
                            ? UserProfileEnum.ADMINISTRATOR
                            : UserProfileEnum.MEMBER;

                    User user = userRepository.getReferenceById(memberId);
                    return UserBoard.builder()
                            .board(board)
                            .user(user)
                            .profile(profile)
                            .starred(false)
                            .build();
                })
                .collect(Collectors.toList());

        userBoardRepository.saveAll(userBoardList);

        return uniqueUserIds.size();
    }

    private Action createActionAndSave(
            BoardColumn column,
            TrelloActionDto actionDto
    ) {

        // Determine assignee ID and get reference if present
        User assignee = null;
        if (actionDto.getAssignee() != null && actionDto.getAssignee().getId() != null) {
            assignee = userRepository.getReferenceById(actionDto.getAssignee().getId());
        }

        // Handle priority - get by label
        CustomFieldOption priorityOption = customFieldService.getCustomFieldOption(
                actionDto.getPriority(),
                CustomFieldType.PRIORITY.toString()
        );

        Action action = Action.builder()
                .title(actionDto.getTitle())
                .description(actionDto.getDescription())
                .dueDate(actionDto.getDueDate())
                .offset(actionDto.getOffset())
                .estimation(actionDto.getEstimation())
                .status(ActionStatusEnum.ACTIVE)
                .boardColumn(column)
                .assignee(assignee)
                .priority(priorityOption)
                .build();

        return actionRepository.save(action);
    }

    private List<ActionMember> createActionMembers(
            Action action,
            List<TrelloMemberDto> members
    ) {
        Set<Long> uniqueMemberIds = new HashSet<>();

        for (var member : members) {
            if (member.getId() != null) {
                uniqueMemberIds.add(member.getId());
            }
        }

        return uniqueMemberIds.stream()
                .map(memberId -> {
                    User user = userRepository.getReferenceById(memberId);
                    return ActionMember.builder()
                            .action(action)
                            .member(user)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Processes custom field definitions from board import.
     * Creates new custom fields and their options as needed.
     *
     * @param customFieldDefinitions List of custom field definitions
     */
    @Transactional
    public void processCustomFieldDefinitions(List<CustomFieldDefinitionDto> customFieldDefinitions) {
        if (customFieldDefinitions == null || customFieldDefinitions.isEmpty()) {
            log.info("No custom field definitions to process");
            return;
        }

        log.info("Start processing {} custom field definitions", customFieldDefinitions.size());

        for (CustomFieldDefinitionDto fieldDef : customFieldDefinitions) {
            if (fieldDef.getType() == null) {
                log.warn("Skipping custom field '{}' with no type mapping", fieldDef.getName());
                continue;
            }

            log.info("Processing custom field: {} of type: {}", fieldDef.getName(), fieldDef.getType());

            // Get or create the custom field
            CustomField customField = customFieldRepository.findByType(fieldDef.getType())
                    .orElseGet(() -> {
                        log.info("Creating new custom field with type: {}", fieldDef.getType());
                        CustomField newField = CustomField.builder()
                                .label(fieldDef.getName())
                                .type(fieldDef.getType())
                                .build();
                        return customFieldRepository.save(newField);
                    });

            // Process options if present
            if (fieldDef.getOptions() != null && !fieldDef.getOptions().isEmpty()) {
                log.info("Processing {} options for custom field: {}", fieldDef.getOptions().size(), fieldDef.getName());

                for (CustomFieldOptionSimplifiedDto optionDto : fieldDef.getOptions()) {
                    if (optionDto.getId() == null) {
                        // Create new option
                        log.info("Creating new option '{}' for custom field: {}", optionDto.getLabel(), fieldDef.getName());
                        CustomFieldOption newOption = CustomFieldOption.builder()
                                .label(optionDto.getLabel())
                                .customField(customField)
                                .build();
                        customFieldOptionRepository.save(newOption);
                    } else {
                        log.info("Option '{}' already exists with id: {}", optionDto.getLabel(), optionDto.getId());
                    }
                }
            }
        }

        log.info("Finished processing custom field definitions");
    }
}
