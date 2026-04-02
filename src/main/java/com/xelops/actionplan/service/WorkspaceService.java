package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.domain.Workspace;
import com.xelops.actionplan.dto.UserPrivilegesDto;
import com.xelops.actionplan.dto.WorkspaceDto;
import com.xelops.actionplan.dto.WorkspaceListingDto;
import com.xelops.actionplan.enumeration.ExistsByEnum;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.WorkspaceMapper;
import com.xelops.actionplan.repository.WorkspaceRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserWorkspaceService userWorkspaceService;
    private final WorkspaceMapper workspaceMapper;
    private final UserHelperService userHelperService;
    private final Messages messages;

    public Page<WorkspaceListingDto> getWorkspacesForCurrentUser(int page, int size) throws NotFoundException {
        log.info("Start service getWorkspacesForCurrentUser");
        Sort sort = Sort.by(
                Sort.Order.desc("updatedAt"),
                Sort.Order.asc("name")
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        UserPrivilegesDto userPrivilegesDto = userHelperService.getConnectedUserDetails();
        Page<Workspace> workspaces = Page.empty();
        if (userPrivilegesDto.role() == UserRoleEnum.ADMIN) {
            Long organizationId = userPrivilegesDto.organization().id();
            workspaces = workspaceRepository.findByOrganizationId(organizationId, pageable);
        } else if (userPrivilegesDto.role() == UserRoleEnum.SIMPLE_USER) {
            workspaces = workspaceRepository.findByUserWorkspacesUserId(userPrivilegesDto.userId(), pageable);
        }
        Page<WorkspaceListingDto> workspaceDtos = workspaces.map(workspaceMapper::toWorkspaceListingDto);
        log.info("End service getWorkspacesForCurrentUser | result size: {}", workspaceDtos.getContent().size());
        return workspaceDtos;
    }

    @Transactional
    public void createUpdateWorkspace(WorkspaceDto workspaceDto) throws NotFoundException {
        log.info("Start service createUpdateWorkspace | id: {}", workspaceDto.id());
        Workspace workspace;
        User connectedUser = null;
        boolean isCreate = workspaceDto.id() == null;
        if (isCreate) {
            //create
            workspace = workspaceMapper.toWorkspace(workspaceDto);
            connectedUser = userHelperService.getConnectedUser();

            // Verify organization access (defense in depth)
            userHelperService.verifyOrganizationAccess(connectedUser.getOrganization().getId());

            workspace.setOrganization(connectedUser.getOrganization());
            workspace.setCreatedBy(connectedUser);
        } else {
            //update
            workspace = getWorkspaceById(workspaceDto.id()); // This already verifies organization
            workspaceMapper.toUpdatedWorkspace(workspaceDto, workspace);
        }
        Workspace savedWorkspace = workspaceRepository.save(workspace);
        if (isCreate) {
            // Add creator of workspace as admin
            userWorkspaceService.saveUserWorkspace(
                    savedWorkspace,
                    connectedUser,
                    UserProfileEnum.ADMINISTRATOR);
        }
        //TODO: Save workspace image
        log.info("End service createUpdateWorkspace");
    }

    public WorkspaceDto getWorkspaceDtoById(Long id) throws NotFoundException {
        log.info("Start service getWorkspaceDtoById | id: {}", id);

        Workspace workspace = getWorkspaceById(id);
        WorkspaceDto workspaceDto = workspaceMapper.toWorkspaceDto(workspace);

        log.info("End service getWorkspaceDtoById | id: {}", workspaceDto.id());
        return workspaceDto;
    }

    public Workspace getWorkspaceById(Long id) throws NotFoundException {
        log.info("Start service getWorkspaceById | id: {}", id);

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_NOT_FOUND_BY_FIELD,
                        ModuleEnum.WORKSPACE.getName(), ExistsByEnum.ID.name(), id)));

        // Verify organization access
        userHelperService.verifyOrganizationAccess(workspace.getOrganization().getId());

        log.info("End service getWorkspaceById | id: {}", workspace.getId());
        return workspace;
    }
}
