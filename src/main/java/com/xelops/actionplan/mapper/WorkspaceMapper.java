package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.Workspace;
import com.xelops.actionplan.dto.WorkspaceDto;
import com.xelops.actionplan.dto.WorkspaceListingDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { UserWorkspaceMapper.class })
public interface WorkspaceMapper {

    Workspace toWorkspace(WorkspaceDto workspaceDto);

    @Mapping(target = "abbreviation", expression = "java(java.util.Arrays.stream(workspace.getName().split(\"\\\\s+\")).map(w -> w.substring(0, 1).toUpperCase()).collect(java.util.stream.Collectors.joining()))")
    @Mapping(target = "membersCount", expression = "java(workspace.getUserWorkspaces() != null ? workspace.getUserWorkspaces().size() : 0)")
    @Mapping(target = "boardsCount", expression = "java(workspace.getBoards() != null ? workspace.getBoards().size() : 0)")
    WorkspaceListingDto toWorkspaceListingDto(Workspace workspace);

    WorkspaceDto toWorkspaceDto(Workspace workspace);

    @Mapping(target = "name")
    @Mapping(target = "description")
    @BeanMapping(ignoreByDefault = true)
    void toUpdatedWorkspace(WorkspaceDto workspaceDto, @MappingTarget Workspace existingWorkspace);


}

