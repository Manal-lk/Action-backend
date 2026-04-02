package com.xelops.actionplan.service;

import com.xelops.actionplan.client.NotificationClient;
import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Action;
import com.xelops.actionplan.domain.BoardColumn;
import com.xelops.actionplan.domain.Organization;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.*;
import com.xelops.actionplan.dto.notification.NewNotificationsDto;
import com.xelops.actionplan.dto.notification.NotificationDataDto;
import com.xelops.actionplan.enumeration.ActionHistoryType;
import com.xelops.actionplan.enumeration.DueIn;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.enumeration.notification.NotificationTemplateEnum;
import com.xelops.actionplan.enumeration.notification.NotificationTypeEnum;
import com.xelops.actionplan.enumeration.notification.PlatformEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.exception.StorageException;
import com.xelops.actionplan.mapper.ActionMapper;
import com.xelops.actionplan.mapper.ActionMetricsMapper;
import com.xelops.actionplan.projections.*;
import com.xelops.actionplan.repository.ActionRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;
    private final ActionMapper actionMapper;
    private final ActionHelperService actionHelperService;
    private final Messages messages;
    private final BoardColumnService boardColumnService;
    private final UserHelperService userHelperService;
    private final ActionMemberService actionMemberService;
    private final NotificationClient notificationClient;
    private final NotificationService notificationService;
    private final CheckListService checkListService;
    private final BoardHelperService boardHelperService;
    private final OrganizationService organizationService;
    private final ActionMetricsMapper actionMetricsMapper;
    private final AttachmentService attachmentService;
    private final HistoryService historyService;

    @Transactional
    public void createUpdateAction(Long boardId, ActionDto actionDto) throws FunctionalException, NotFoundException {
        log.info("Start service createUpdateAction | boardId: {} | action title: {}", boardId, actionDto.getTitle());

        // SECURITY CHECK FIRST: Verify organization access to board immediately
        var board = boardHelperService.getById(boardId);
        userHelperService.verifyOrganizationAccess(board.getWorkspace().getOrganization().getId());

        if (!userHelperService.hasAccessToBoard(boardId)) {
            throw new FunctionalException(messages.get(GlobalConstants.USER_DOES_NOT_HAVE_ACCESS_TO_BOARD_ERROR, boardId));
        }

        if (!boardColumnService.exists(actionDto.getBoardColumn().getId())) {
            throw new NotFoundException(messages.get(GlobalConstants.BOARD_COLUMN_NOT_FOUND_ERROR, actionDto.getBoardColumn().getId()));
        }

        if (actionDto.getAssignee() != null && !userHelperService.hasAccessToBoard(actionDto.getAssignee().getId(), boardId)) {
            throw new NotFoundException(messages.get(GlobalConstants.USER_DOES_NOT_HAVE_ACCESS_TO_BOARD_ERROR, boardId));
        }

        Action existingAction = null;
        if (Objects.nonNull(actionDto.getId())) {
            existingAction = actionRepository.findById(actionDto.getId())
                    .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ACTION_NOT_FOUND_ERROR, actionDto.getId())));
            // SECURITY CHECK: Verify organization access to existing action
            userHelperService.verifyOrganizationAccess(existingAction.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());
        } else if (actionRepository.existsByTitleAndBoardColumn_Id(actionDto.getTitle(), actionDto.getBoardColumn().getId())) {
            throw new FunctionalException(messages.get(GlobalConstants.ACTION_TITLE_ALREADY_EXISTS_IN_BOARD_COLUMN_ERROR, actionDto.getTitle(), actionDto.getBoardColumn().getName()));
        }

        Action actionToSave = actionMapper.toAction(actionDto);
        if (existingAction != null) {
            actionToSave.setId(existingAction.getId());

            ActionDto existingActionDto = actionMapper.toActionDto(existingAction);
            historyService.addHistoryEventsFromDiff(existingAction.getId(), actionDto, existingActionDto);
        }

        if (actionDto.getAssignee() != null) {
            actionToSave.setAssignee(User.builder().id(actionDto.getAssignee().getId()).build());
        }
        actionToSave.setMembers(null); // Members are handled separately
        Action savedAction = actionRepository.save(actionToSave);
        if (existingAction == null) {
            historyService.addHistoryEvent(savedAction.getId(), ActionHistoryType.ACTION_CREATED, null, savedAction.getTitle());
        }
        notifyMemberTagIds(actionDto.getMemberTagIds(), savedAction, boardId);
        actionMemberService.addActionMembers(boardId, savedAction.getId(), actionDto.getMembers().stream().map(UserSimplifiedDto::getId).toList());
        if (CollectionUtils.isNotEmpty(actionDto.getCheckLists())) {
            checkListService.createCheckListsForAction(savedAction.getId(), actionDto.getCheckLists());
        }
        log.info("End service createUpdateAction | boardId: {} | action title: {} | saved action id: {}", boardId, actionDto.getTitle(), savedAction.getId());
    }

    private void notifyMemberTagIds(List<Long> memberTagIds, Action action, Long boardId) throws NotFoundException {
        log.info("Start service notifyMemberTagIds | memberTagIds: {} | actionId: {}", memberTagIds, action.getId());
        if (!memberTagIds.isEmpty()) {
            UserPrivilegesDto connectedUserDetails = userHelperService.getConnectedUserDetails();
            Map<Long, User> users = userHelperService.findUsersByIds(memberTagIds);
            final var notificationsData = memberTagIds.stream()
                    .map(user -> NotificationDataDto.builder()
                            .type(NotificationTypeEnum.APP)
                            .recipientEmail(users.get(user).getEmail())
                            .recipientId(users.get(user).getKeycloakId())
                            .titleData(Map.of())
                            .entityId(action.getId())
                            .redirectionPath("/boards/" + boardId)
                            .bodyData(Map.of(
                                    "User", Optional.ofNullable(connectedUserDetails.email()).orElse(connectedUserDetails.username()),
                                    "ActionName", action.getTitle()
                            ))
                            .build()
                    )
                    .toList();
            final var emailNotificationsData = memberTagIds.stream()
                    .map(user -> NotificationDataDto.builder()
                            .type(NotificationTypeEnum.EMAIL)
                            .recipientEmail(users.get(user).getEmail())
                            .recipientId(users.get(user).getKeycloakId())
                            .titleData(Map.of())
                            .bodyData(Map.of(
                                    "User", Optional.ofNullable(connectedUserDetails.email()).orElse(connectedUserDetails.username()),
                                    "ActionName", action.getTitle(),
                                    GlobalConstants.NOTIFICATION_APP_LINK_KEY, organizationService.getUrlPlatform() + "/boards/" + boardId.toString()
                            ))
                            .build()
                    )
                    .toList();
            final var newNotification = NewNotificationsDto.builder()
                    .platformId(PlatformEnum.ACTIONS.name())
                    .template(NotificationTemplateEnum.USER_MENTIONED_IN_ACTION_NOTIF)
                    .notificationsData(notificationsData)
                    .build();
            final var newEmailNotification = NewNotificationsDto.builder()
                    .platformId(PlatformEnum.ACTIONS.name())
                    .template(NotificationTemplateEnum.USER_MENTIONED_IN_ACTION)
                    .notificationsData(emailNotificationsData)
                    .build();
            try {
                notificationClient.create(newNotification);
                notificationClient.create(newEmailNotification);
                notificationsData.stream()
                        .map(NotificationDataDto::recipientId)
                        .forEach(notificationService::notifyUser);
            } catch (Exception e) {
                log.error("Error Service : Sending notification", e);
            }
        }
        log.info("End service notifyMemberTagIds | memberTagIds: {} | actionId: {}", memberTagIds, action.getId());
    }

    @Transactional
    public ActionDto getAction(Long boardId, Long actionId) throws NotFoundException {
        log.info("Start service getAction | boardId: {} | actionId: {}", boardId, actionId);

        // SECURITY CHECK FIRST: Fetch and verify organization access immediately
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ACTION_NOT_FOUND_ERROR, actionId)));
        userHelperService.verifyOrganizationAccess(action.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());

        actionHelperService.throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(boardId, actionId);
        ActionDto actionDto = actionMapper.toActionDto(action);
        log.info("End service getAction | boardId: {} | actionId: {} | result id: {}", boardId, actionId, actionDto.getId());
        return actionDto;
    }

    public List<Action> findAllByBoardIdAndFilterOptions(Long boardId, ActionFilterDto actionFilter) {
        log.info("Start service findAllByBoardIdAndFilterOptions | boardId: {} | actionFilter: {}", boardId, actionFilter);
        Map<DueIn, DateRangeDto> dueInRangeMap = Map.of(
                DueIn.TODAY, DateRangeDto.builder().startDate(LocalDate.now()).endDate(LocalDate.now()).build(),
                DueIn.THIS_WEEK, DateRangeDto.builder().startDate(LocalDate.now().with(DayOfWeek.MONDAY)).endDate(LocalDate.now().with(DayOfWeek.SUNDAY)).build(),
                DueIn.THIS_MONTH, DateRangeDto.builder().startDate(LocalDate.now().withDayOfMonth(1)).endDate(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())).build(),
                DueIn.OVERDUE, DateRangeDto.builder().startDate(null).endDate(LocalDate.now().minusDays(1)).build()
        );

        DateRangeDto dueInRange = actionFilter.dueIn() != null ?
                dueInRangeMap.getOrDefault(actionFilter.dueIn(), DateRangeDto.builder().build()) :
                DateRangeDto.builder().startDate(actionFilter.startDueDate()).endDate(actionFilter.endDueDate()).build();

        List<Action> actions = actionRepository.findAllByBoardIdAndFilterOptions(
                boardId,
                actionFilter.columnId(),
                actionFilter.assigneeIds(),
                dueInRange.startDate(),
                dueInRange.endDate(),
                Objects.equals(actionFilter.dueIn(), DueIn.OVERDUE) ? false : null // If filtering for overdue, we don't want to include completedRate actions as well
        );

        log.info("End service findAllByBoardIdAndFilterOptions | boardId: {} | actionFilter: {} | result size: {}", boardId, actionFilter, actions.size());
        return actions;
    }

    public void deleteAction(Long boardId, Long actionId) throws NotFoundException {
        log.info("Start service deleteAction | boardId: {} | actionId: {}", boardId, actionId);

        // SECURITY CHECK FIRST: Fetch and verify organization access immediately
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ACTION_NOT_FOUND_ERROR, actionId)));
        userHelperService.verifyOrganizationAccess(action.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());

        actionHelperService.throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(boardId, actionId);
        actionMemberService.removeActionMembers(actionId);
        actionRepository.deleteById(actionId);
        log.info("End service deleteAction | boardId: {} | actionId: {}", boardId, actionId);
    }

    @Transactional
    public void moveAction(Long boardId, Long actionId, Long fromColumnId, Long toColumnId, Integer insertAtIndex) throws NotFoundException {
        log.info("Start service moveAction | boardId: {} | actionId: {} | fromColumnId: {} | toColumnId: {} | insertAtIndex: {}",
                boardId, actionId, fromColumnId, toColumnId, insertAtIndex);

        // SECURITY CHECK FIRST: Fetch and verify organization access immediately
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ACTION_NOT_FOUND_ERROR, actionId)));
        userHelperService.verifyOrganizationAccess(action.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());

        actionHelperService.throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(boardId, actionId);
        boardHelperService.hasAllColumnsOrThrow(boardId, fromColumnId, toColumnId);

        action.setBoardColumn(BoardColumn.builder().id(toColumnId).build());
        List<Action> toColumnActions = new ArrayList<>(
                actionRepository.findAllByBoardColumn_IdOrderByOffset(toColumnId).stream().filter(a -> !Objects.equals(a.getId(), actionId)).toList()
        );
        toColumnActions.add(insertAtIndex, action);
        for (int i = 0; i < toColumnActions.size(); i++) {
            Action a = toColumnActions.get(i);
            a.setOffset(i);
        }
        actionRepository.saveAll(toColumnActions);

        log.info("End service moveAction | boardId: {} | actionId: {}", boardId, actionId);
    }

    @Transactional
    public void toggleActionCompletion(Long boardId, Long actionId, Boolean completed) throws NotFoundException {
        log.info("Start service toggleActionCompletion | boardId: {} | actionId: {} | completedRate: {}", boardId, actionId, completed);
        actionHelperService.throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(boardId, actionId);

        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ACTION_NOT_FOUND_ERROR, actionId)));

        action.setCompleted(completed);
        action.setCompletionDate(completed ? LocalDate.now() : null);
        actionRepository.save(action);

        log.info("End service toggleActionCompletion | boardId: {} | actionId: {} | completedRate: {}", boardId, actionId, completed);
    }

    public ActionStatisticsDto getActionStatisticsProjection() throws NotFoundException {
        log.info("Start service getActionStatisticsProjection");
        User connectedUser = userHelperService.getConnectedUser();
        Organization organization = organizationService.getCurrentOrganization();
        ActionStatisticsProjection projection = actionRepository.getActionStatisticsProjection(connectedUser.getId(),
                UserRoleEnum.ADMIN.equals(connectedUser.getRole()), organization.getId());
        ActionStatisticsDto dto = actionMetricsMapper.toActionStatisticsDto(projection);
        log.info("End service getActionStatisticsProjection | total: {} | completedRate: {} | overdue: {} | completionRate: {}",
                dto.total(), dto.completed(), dto.overdue(), dto.completionRate());
        return dto;
    }

    public ActionStatusBreakdownDto getActionStatusBreakdown() throws NotFoundException {
        log.info("Start service getActionStatusBreakdown");
        User connectedUser = userHelperService.getConnectedUser();
        Organization organization = organizationService.getCurrentOrganization();
        var projection = actionRepository.getActionStatusBreakdown(connectedUser.getId(),
                UserRoleEnum.ADMIN.equals(connectedUser.getRole()), organization.getId());
        ActionStatusBreakdownDto dto = actionMetricsMapper.toActionStatusBreakdownDto(projection);
        log.info("End service getActionStatusBreakdown | openRate: {} | completedRate: {}",
                dto.openRate(), dto.completedRate());
        return dto;
    }

    public OverdueActionsBreakdownDto getOverdueActionsBreakdown() throws NotFoundException {
        log.info("Start service getOverdueActionsBreakdown");
        User connectedUser = userHelperService.getConnectedUser();
        Organization organization = organizationService.getCurrentOrganization();
        var projection = actionRepository.getOverdueActionsBreakdown(connectedUser.getId(),
                UserRoleEnum.ADMIN.equals(connectedUser.getRole()), organization.getId());
        OverdueActionsBreakdownDto dto = actionMetricsMapper.toOverdueActionsBreakdownDto(projection);
        log.info("End service getOverdueActionsBreakdown | 1-3: {} | 4-7: {} | 8-14: {} | 15+: {}",
                dto.oneToThreeDays(), dto.fourToSevenDays(), dto.eightToFourteenDays(), dto.fifteenPlusDays());
        return dto;
    }

    public AverageResolutionTimeDto getAverageResolutionTime() throws NotFoundException {
        log.info("Start service getAverageResolutionTime");
        User connectedUser = userHelperService.getConnectedUser();
        Organization organization = organizationService.getCurrentOrganization();
        var projection = actionRepository.getAverageResolutionTimeNative(
                connectedUser.getId(),
                UserRoleEnum.ADMIN.equals(connectedUser.getRole()),
                organization.getId()
        );
        AverageResolutionTimeDto dto = new AverageResolutionTimeDto(projection);
        log.info("End service getAverageResolutionTime | averageResolutionTime: {}", dto.averageResolutionTime());
        return dto;
    }

    public List<TopAssigneeDto> getTopAssignees() throws NotFoundException {
        log.info("Start service getTopAssignees");
        Organization organization = organizationService.getCurrentOrganization();
        List<TopAssigneeProjection> projections = actionRepository.getTopAssignees(organization.getId());
        List<TopAssigneeDto> result = actionMetricsMapper.toTopAssigneeDtoList(projections);
        log.info("End service getTopAssignees | count: {}", result.size());
        return result;
    }

    public List<UpcomingDeadlineDto> getUpcomingDeadlines() throws NotFoundException {
        log.info("Start service getUpcomingDeadlines");
        Organization organization = organizationService.getCurrentOrganization();
        List<UpcomingDeadlineProjection> projections = actionRepository.getUpcomingDeadlines(organization.getId(), LocalDate.now().plusDays(7));
        List<UpcomingDeadlineDto> result = actionMetricsMapper.toUpcomingDeadlineDtoList(projections);
        log.info("End service getUpcomingDeadlines | count: {}", result.size());
        return result;
    }

    public List<CompletionRateByPriorityDto> getCompletionRateByPriority() throws NotFoundException {
        log.info("Start service getCompletionRateByPriority");
        Organization organization = organizationService.getCurrentOrganization();
        List<CompletionRateByPriorityProjection> projections = actionRepository.getCompletionRateByPriority(organization.getId());
        List<CompletionRateByPriorityDto> result = actionMetricsMapper.toCompletionRateByPriorityDtoList(projections);
        log.info("End service getCompletionRateByPriority | count: {}", result.size());
        return result;
    }

    public List<ActionsTrendDto> getActionsTrend() throws NotFoundException {
        log.info("Start service getActionsTrend");
        Organization organization = organizationService.getCurrentOrganization();
        List<ActionsTrendProjection> projections = actionRepository.getActionsTrend(organization.getId());
        List<ActionsTrendDto> result = actionMetricsMapper.toActionsTrendDtoList(projections);
        log.info("End service getActionsTrend | count: {}", result.size());
        return result;
    }

    public List<AttachmentDto> addActionAttachments(Long boardId, Long actionId, MultipartFile[] files) throws NotFoundException, StorageException {
        log.info("Start service addActionAttachments | boardId: {} | actionId: {} | files count: {}", boardId, actionId, files.length);
        actionHelperService.throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(boardId, actionId);
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ACTION_NOT_FOUND_ERROR, actionId)));

        List<AttachmentDto> attachmentDtoList = attachmentService.storeAttachments(action, files);

        log.info("End service addActionAttachments | boardId: {} | actionId: {} | files count: {}", boardId, actionId, files.length);
        return attachmentDtoList;
    }

    public List<AttachmentDto> getActionAttachments(Long boardId, Long actionId) throws NotFoundException {
        log.info("Start service getActionAttachments | boardId: {} | actionId: {}", boardId, actionId);
        List<AttachmentDto> attachmentDtoList = attachmentService.getAttachmentsByActionId(boardId, actionId);
        log.info("End service getActionAttachments | boardId: {} | actionId: {} | attachments count: {}",
                 boardId, actionId, attachmentDtoList.size());
        return attachmentDtoList;
    }

    public List<HistoryDto> getActionHistory(Long boardId, Long actionId) throws NotFoundException {
        log.info("Start service getActionHistory | boardId: {} | actionId: {}", boardId, actionId);
        actionHelperService.throwIfConnectedUserHasNotAccessToBoardOrActionDoesNotExist(boardId, actionId);
        List<HistoryDto> history = historyService.getHistoryByActionId(actionId);
        log.info("End service getActionHistory | boardId: {} | actionId: {} | history count: {}", boardId, actionId, history.size());
        return history;
    }
}
