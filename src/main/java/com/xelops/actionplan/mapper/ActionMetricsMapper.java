package com.xelops.actionplan.mapper;

import com.xelops.actionplan.dto.ActionStatisticsDto;
import com.xelops.actionplan.dto.ActionStatusBreakdownDto;
import com.xelops.actionplan.dto.OverdueActionsBreakdownDto;
import com.xelops.actionplan.dto.TopAssigneeDto;
import com.xelops.actionplan.dto.UpcomingDeadlineDto;
import com.xelops.actionplan.dto.CompletionRateByPriorityDto;
import com.xelops.actionplan.dto.ActionsTrendDto;
import com.xelops.actionplan.projections.ActionStatisticsProjection;
import com.xelops.actionplan.projections.ActionStatusBreakdownProjection;
import com.xelops.actionplan.projections.OverdueActionsBreakdownProjection;
import com.xelops.actionplan.projections.TopAssigneeProjection;
import com.xelops.actionplan.projections.UpcomingDeadlineProjection;
import com.xelops.actionplan.projections.CompletionRateByPriorityProjection;
import com.xelops.actionplan.projections.ActionsTrendProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.List;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface ActionMetricsMapper {
    @Mapping(target = "completionRate", source = "completionRate", qualifiedByName = "roundToTwoDecimals")
    ActionStatisticsDto toActionStatisticsDto(ActionStatisticsProjection projection);

    @Mapping(target = "openRate", source = "openRate", qualifiedByName = "roundToTwoDecimals")
    @Mapping(target = "completedRate", source = "completedRate", qualifiedByName = "roundToTwoDecimals")
    ActionStatusBreakdownDto toActionStatusBreakdownDto(ActionStatusBreakdownProjection projection);
    OverdueActionsBreakdownDto toOverdueActionsBreakdownDto(OverdueActionsBreakdownProjection projection);

    TopAssigneeDto toTopAssigneeDto(TopAssigneeProjection projection);
    List<TopAssigneeDto> toTopAssigneeDtoList(List<TopAssigneeProjection> projections);

    UpcomingDeadlineDto toUpcomingDeadlineDto(UpcomingDeadlineProjection projection);
    List<UpcomingDeadlineDto> toUpcomingDeadlineDtoList(List<UpcomingDeadlineProjection> projections);

    CompletionRateByPriorityDto toCompletionRateByPriorityDto(CompletionRateByPriorityProjection projection);
    List<CompletionRateByPriorityDto> toCompletionRateByPriorityDtoList(List<CompletionRateByPriorityProjection> projections);

    @Mapping(target = "monthName", source = ".", qualifiedByName = "mapToMonthName")
    ActionsTrendDto toActionsTrendDto(ActionsTrendProjection projection);
    List<ActionsTrendDto> toActionsTrendDtoList(List<ActionsTrendProjection> projections);

    @Named("roundToTwoDecimals")
    default Double roundToTwoDecimals(Double value) {
        if (value == null) {
            return null;
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Named("mapToMonthName")
    default String mapToMonthName(ActionsTrendProjection projection) {
        if (projection == null || projection.getMonth() == null) {
            return null;
        }

        String monthName = Month.of(projection.getMonth())
                .getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);
        String shortYear = String.valueOf(projection.getYear()).substring(2);

        return monthName + " " + shortYear;
    }
}
