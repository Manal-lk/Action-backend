package com.xelops.actionplan.service;

import com.azure.core.implementation.util.ObjectsUtil;
import com.xelops.actionplan.domain.CustomFieldOption;
import com.xelops.actionplan.domain.History;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.*;
import com.xelops.actionplan.enumeration.ActionHistoryChangeType;
import com.xelops.actionplan.enumeration.ActionHistoryType;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.HistoryMapper;
import com.xelops.actionplan.repository.CustomFieldOptionRepository;
import com.xelops.actionplan.repository.HistoryRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryMapper historyMapper;
    private final HistoryRepository historyRepository;
    private final UserHelperService userHelperService;
    private final BoardColumnService boardColumnService;
    private final CustomFieldOptionRepository customFieldOptionRepository;

    @Transactional
    public void addHistoryEventsFromDiff(Long actionId, @NotNull ActionDto newAction, @NotNull ActionDto oldAction) throws NotFoundException {
        log.info("Start service addHistoryEventsFromDiff | newAction: {} | oldAction: {}", newAction, oldAction);
        List<HistoryEventDto> historyEvents = getHistoryEventsFromDiff(newAction, oldAction);
        List<History> historyRecords = new ArrayList<>();
        for (HistoryEventDto historyEvent : historyEvents) {
            historyRecords.add(
                    getHistoryRecordFromHistoryEvent(actionId, historyEvent)
            );
        }
        historyRepository.saveAll(historyRecords);
        log.info("End service addHistoryEventsFromDiff | newAction: {} | oldAction: {} | historyEvents: {}", newAction, oldAction, historyEvents);
    }

    public void addHistoryEvent(Long actionId, ActionHistoryType historyType, String oldData, String newData) throws NotFoundException {
        log.info("Start service addHistoryEvent | actionId: {} | historyType: {} | oldData: {} | newData: {}", actionId, historyType, oldData, newData);
        HistoryEventDto historyEvent = HistoryEventDto.builder()
                .actionHistoryType(historyType)
                .oldData(oldData)
                .newData(newData)
                .build();
        History historyRecord = getHistoryRecordFromHistoryEvent(actionId, historyEvent);
        historyRepository.save(historyRecord);
        log.info("End service addHistoryEvent | actionId: {} | historyType: {} | oldData: {} | newData: {}", actionId, historyType, oldData, newData);
    }

    public History getHistoryRecordFromHistoryEvent(Long actionId, HistoryEventDto historyEvent) throws NotFoundException {
        log.info("Start service addHistoryEvent | actionId: {} | historyEvent: {}", actionId, historyEvent);
        History historyRecord = historyMapper.toHistory(historyEvent, actionId);
        historyRecord.setConcernedUser(
                Optional.ofNullable(historyEvent.concernedUserId())
                        .map(userHelperService::findUserById)
                        .orElse(null)
        );
        historyRecord.setOldConcernedUser(
                Optional.ofNullable(historyEvent.oldConcernedUserId())
                        .map(userHelperService::findUserById)
                        .orElse(null)
        );

        historyRecord.setSourceColumn(
                Optional.ofNullable(historyEvent.sourceColumnId())
                        .map(boardColumnService::findById)
                        .orElse(null)
        );
        historyRecord.setTargetColumn(
                Optional.ofNullable(historyEvent.targetColumnId())
                        .map(boardColumnService::findById)
                        .orElse(null)
        );

        historyRecord.setCreatedBy(userHelperService.getConnectedUser());
        historyRecord.setCreatedAt(LocalDateTime.now());

        log.info("End service addHistoryEvent | actionId: {} | historyEvent: {} | historyRecord: {}", actionId, historyEvent, historyRecord);
        return historyRecord;
    }

    private List<HistoryEventDto> getHistoryEventsFromDiff(@NotNull ActionDto newAction, @NotNull ActionDto oldAction) {
        log.info("Start service getHistoryRecordsFromDiff | newAction: {} | oldAction: {}", newAction, oldAction);
        List<HistoryEventDto> historyEvents = new ArrayList<>();

        if (!Objects.equals(
                Optional.ofNullable(newAction.getTitle()).orElse(""),
                Optional.ofNullable(oldAction.getTitle()).orElse("")
        )) {
            historyEvents.add(
                    HistoryEventDto.builder()
                            .actionHistoryType(ActionHistoryType.TITLE_CHANGE)
                            .oldData(oldAction.getTitle())
                            .newData(newAction.getTitle())
                            .build()
            );
        }

        if (!Objects.equals(
                Optional.ofNullable(newAction.getDescription()).orElse(""),
                Optional.ofNullable(oldAction.getDescription()).orElse("")
        )) {
            historyEvents.add(
                    HistoryEventDto.builder()
                            .actionHistoryType(ActionHistoryType.DESCRIPTION_CHANGE)
                            .oldData(oldAction.getDescription())
                            .newData(newAction.getDescription())
                            .build()
            );
        }

        if (!Objects.equals(
                newAction.getDueDate(),
                oldAction.getDueDate()
        )) {
            historyEvents.add(
                    HistoryEventDto.builder()
                            .actionHistoryType(ActionHistoryType.DUE_DATE_CHANGE)
                            .oldData(oldAction.getDueDate() != null ? oldAction.getDueDate().format(GlobalConstants.DATE_TIME_FORMATTER) : null)
                            .newData(newAction.getDueDate() != null ? newAction.getDueDate().format(GlobalConstants.DATE_TIME_FORMATTER) : null)
                            .build()
            );
        }

        if (!Objects.equals(
                Optional.ofNullable(newAction.getPriority()).map(CustomFieldOptionSimplifiedDto::getId).orElse(null),
                Optional.ofNullable(oldAction.getPriority()).map(CustomFieldOptionSimplifiedDto::getId).orElse(null)
        )) {
            historyEvents.add(
                    HistoryEventDto.builder()
                            .actionHistoryType(ActionHistoryType.PRIORITY_CHANGE)
                            .oldData(Optional.ofNullable(oldAction.getPriority()).map(CustomFieldOptionSimplifiedDto::getId).map(Object::toString).orElse(null))
                            .newData(Optional.ofNullable(newAction.getPriority()).map(CustomFieldOptionSimplifiedDto::getId).map(Object::toString).orElse(null))
                            .build()
            );
        }

        if (!Objects.equals(
                newAction.getEstimation(),
                oldAction.getEstimation()
        )) {
            historyEvents.add(
                    HistoryEventDto.builder()
                            .actionHistoryType(ActionHistoryType.ESTIMATION_CHANGE)
                            .oldData(Optional.ofNullable(oldAction.getEstimation()).map(Object::toString).orElse(null))
                            .newData(Optional.ofNullable(newAction.getEstimation()).map(Object::toString).orElse(null))
                            .build()
            );
        }

        if (!Objects.equals(
                Optional.ofNullable(newAction.getAssignee()).map(UserSimplifiedDto::getId).orElse(null),
                Optional.ofNullable(oldAction.getAssignee()).map(UserSimplifiedDto::getId).orElse(null)
        )) {
            historyEvents.add(
                    HistoryEventDto.builder()
                            .actionHistoryType(ActionHistoryType.ASSIGNEE_CHANGE)
                            .oldConcernedUserId(Optional.ofNullable(oldAction.getAssignee()).map(UserSimplifiedDto::getId).orElse(null))
                            .concernedUserId(Optional.ofNullable(newAction.getAssignee()).map(UserSimplifiedDto::getId).orElse(null))
                            .oldData(Optional.ofNullable(oldAction.getAssignee()).map(UserSimplifiedDto::getId).map(Object::toString).orElse(null))
                            .newData(Optional.ofNullable(newAction.getAssignee()).map(UserSimplifiedDto::getId).map(Object::toString).orElse(null))
                            .build()
            );
        }
        if (!Objects.equals(
                Optional.ofNullable(newAction.getBoardColumn()).map(BoardColumnSimplifiedDto::getId).orElse(null),
                Optional.ofNullable(oldAction.getBoardColumn()).map(BoardColumnSimplifiedDto::getId).orElse(null)
        )) {
            historyEvents.add(
                    HistoryEventDto.builder()
                            .actionHistoryType(ActionHistoryType.COLUMN_CHANGE)
                            .sourceColumnId(Optional.ofNullable(oldAction.getBoardColumn()).map(BoardColumnSimplifiedDto::getId).orElse(null))
                            .targetColumnId(Optional.ofNullable(newAction.getBoardColumn()).map(BoardColumnSimplifiedDto::getId).orElse(null))
                            .oldData(Optional.ofNullable(oldAction.getBoardColumn()).map(BoardColumnSimplifiedDto::getId).map(Object::toString).orElse(null))
                            .newData(Optional.ofNullable(newAction.getBoardColumn()).map(BoardColumnSimplifiedDto::getId).map(Object::toString).orElse(null))
                            .build()
            );
        }

        Collection<Long> membersIntersection = CollectionUtils.intersection(
                newAction.getMembers().stream().map(UserSimplifiedDto::getId).toList(),
                oldAction.getMembers().stream().map(UserSimplifiedDto::getId).toList()
        );

        Collection<Long> membersAdded = CollectionUtils.disjunction(
                newAction.getMembers().stream().map(UserSimplifiedDto::getId).toList(),
                membersIntersection
        );

        Collection<Long> membersRemoved = CollectionUtils.disjunction(
                oldAction.getMembers().stream().map(UserSimplifiedDto::getId).toList(),
                membersIntersection
        );

        if (!membersAdded.isEmpty()) {
            historyEvents.addAll(
                    membersAdded.stream()
                            .map(memberId -> HistoryEventDto.builder()
                                    .actionHistoryType(ActionHistoryType.MEMBER_ADD)
                                    .concernedUserId(memberId)
                                    .newData(memberId.toString())
                                    .build())
                            .toList()
            );
        }

        if (!membersRemoved.isEmpty()) {
            historyEvents.addAll(
                    membersRemoved.stream()
                            .map(memberId -> HistoryEventDto.builder()
                                    .actionHistoryType(ActionHistoryType.MEMBER_REMOVE)
                                    .oldConcernedUserId(memberId)
                                    .oldData(memberId.toString())
                                    .build())
                            .toList()
            );
        }

        log.info("End service getHistoryRecordsFromDiff | newAction: {} | oldAction: {} | historyEvents: {}", newAction, oldAction, historyEvents);
        return historyEvents;
    }

    public List<HistoryDto> getHistoryByActionId(Long actionId) {
        log.info("Start service getHistoryByActionId | actionId: {}", actionId);
        List<History> historyRecords = historyRepository.findByActionIdOrderByCreatedAtDesc(actionId);

        List<HistoryDto> history = historyRecords.stream()
                .filter(historyRecord -> historyRecord.getActionHistoryType().isVisible())
                .map(historyRecord -> {

                    boolean alternate = historyRecord.getCreatedBy().getId().equals(
                            Optional.ofNullable(historyRecord.getConcernedUser())
                                    .map(User::getId)
                                    .orElse(null)
                    ) || historyRecord.getCreatedBy().getId().equals(
                            Optional.ofNullable(historyRecord.getOldConcernedUser())
                                    .map(User::getId)
                                    .orElse(null)
                    ) || (
                            historyRecord.getActionHistoryType().getChangeType(false).equals(ActionHistoryChangeType.DIFF) &&
                                    ObjectUtils.anyNull(historyRecord.getOldData(), historyRecord.getNewData())
                    );

                    String details = switch (historyRecord.getActionHistoryType().getChangeType(alternate)) {
                        case DIFF -> historyRecord.getActionHistoryType().getDetails(alternate).formatted(
                                historyRecord.getCreatedBy().getFullname(),
                                getHistoryOldOrNewDataLabel(historyRecord.getActionHistoryType(), historyRecord, ActionHistoryChangeType.NOT_DIFF_OLD_DATA),
                                getHistoryOldOrNewDataLabel(historyRecord.getActionHistoryType(), historyRecord, ActionHistoryChangeType.NOT_DIFF_NEW_DATA)
                        );
                        case NOT_DIFF_NEW_DATA -> historyRecord.getActionHistoryType().getDetails(alternate).formatted(
                                historyRecord.getCreatedBy().getFullname(),
                                getHistoryOldOrNewDataLabel(historyRecord.getActionHistoryType(), historyRecord, ActionHistoryChangeType.NOT_DIFF_NEW_DATA)
                        );
                        case NOT_DIFF_OLD_DATA -> historyRecord.getActionHistoryType().getDetails(alternate).formatted(
                                historyRecord.getCreatedBy().getFullname(),
                                getHistoryOldOrNewDataLabel(historyRecord.getActionHistoryType(), historyRecord, ActionHistoryChangeType.NOT_DIFF_OLD_DATA)
                        );
                    };
                    return HistoryDto.builder()
                            .details(details)
                            .concernedUser(
                                    UserDto.builder()
                                            .id(historyRecord.getCreatedBy().getId())
                                            .fullname(historyRecord.getCreatedBy().getFullname())
                                            .build()
                            )
                            .timestamp(historyRecord.getCreatedAt()).build();
                }).toList();

        log.info("End service getHistoryByActionId | actionId: {} | history size: {}", actionId, history.size());
        return history;
    }

    private String getHistoryOldOrNewDataLabel(ActionHistoryType historyType, History historyRecord, ActionHistoryChangeType changeType) {
        log.info("Start service getHistoryOldOrNewDataLabel | historyType: {} | historyRecord id: {} | changeType: {}", historyType, historyRecord.getId(), changeType);
        if (historyType == ActionHistoryType.ASSIGNEE_CHANGE) {
            User concernedAssignee = changeType.equals(ActionHistoryChangeType.NOT_DIFF_NEW_DATA) ? historyRecord.getConcernedUser() : historyRecord.getOldConcernedUser();
            return Optional.ofNullable(concernedAssignee)
                    .map(User::getFullname)
                    .orElse("??");
        }
        if (historyType == ActionHistoryType.COLUMN_CHANGE) {
            return changeType.equals(ActionHistoryChangeType.NOT_DIFF_NEW_DATA) ? historyRecord.getTargetColumn().getName() : historyRecord.getSourceColumn().getName();
        }
        if (historyType == ActionHistoryType.MEMBER_ADD) {
            return historyRecord.getConcernedUser().getFullname();
        }
        if (historyType == ActionHistoryType.MEMBER_REMOVE) {
            return historyRecord.getOldConcernedUser().getFullname();
        }
        if (historyType == ActionHistoryType.PRIORITY_CHANGE) {
            if (changeType.equals(ActionHistoryChangeType.NOT_DIFF_NEW_DATA) && historyRecord.getNewData() == null) {
                return "??";
            }
            if (changeType.equals(ActionHistoryChangeType.NOT_DIFF_OLD_DATA) && historyRecord.getOldData() == null) {
                return "??";
            }
            CustomFieldOption concernedPriority = changeType.equals(ActionHistoryChangeType.NOT_DIFF_NEW_DATA) ?
                    customFieldOptionRepository.findById(Optional.of(historyRecord.getNewData()).map(Long::valueOf).orElse(null)).orElse(null) :
                    customFieldOptionRepository.findById(
                            Objects.requireNonNull(Optional.ofNullable(historyRecord.getOldData()).map(Long::valueOf).orElse(null))
                    ).orElse(null);
            return Optional.ofNullable(concernedPriority).map(CustomFieldOption::getLabel).orElse("??");
        }
        if (historyType == ActionHistoryType.ESTIMATION_CHANGE) {
            return changeType.equals(ActionHistoryChangeType.NOT_DIFF_NEW_DATA) ?
                    Optional.ofNullable(historyRecord.getNewData()).map(estimation -> estimation + " h").orElse("??") :
                    Optional.ofNullable(historyRecord.getOldData()).map(estimation -> estimation + " h").orElse("??");
        }
        return changeType.equals(ActionHistoryChangeType.NOT_DIFF_NEW_DATA) ? historyRecord.getNewData() : historyRecord.getOldData();
    }
}
