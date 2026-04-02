package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Page<Workspace> findByOrganizationId(Long organizationId, Pageable pageable);

    Page<Workspace> findByUserWorkspacesUserId(Long userId, Pageable pageable);

}
