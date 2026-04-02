package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.UserWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWorkspaceRepository extends JpaRepository<UserWorkspace, Long> {
    Optional<UserWorkspace> findByUserIdAndWorkspaceId(Long userId, Long workspaceId);

    List<UserWorkspace> findAllByWorkspaceId(Long workspaceId);
}
