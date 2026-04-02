package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.UserInvitation;
import com.xelops.actionplan.enumeration.UserInvitationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInvitationRepository extends JpaRepository<UserInvitation, Long> {
    Optional<UserInvitation> findByTokenAndStatus(String token, UserInvitationStatusEnum status);

    List<UserInvitation> findByWorkspaceIdAndStatus(Long workspaceId, UserInvitationStatusEnum status);

    List<UserInvitation> findByBoardIdAndStatus(Long boardId, UserInvitationStatusEnum status);

    Optional<UserInvitation> findByEmailAndStatusAndBoardId(String email, UserInvitationStatusEnum status, Long boardId);
    Optional<UserInvitation> findByEmailAndStatusAndWorkspaceId(String email, UserInvitationStatusEnum status, Long workspaceId);
}

