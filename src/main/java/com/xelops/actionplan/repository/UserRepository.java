package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT CASE WHEN COUNT(userBoard) > 0 THEN true ELSE false END " +
            "FROM UserBoard userBoard " +
            "WHERE userBoard.board.id = :boardId AND userBoard.user.id = :userId")
    boolean hasAccess(Long boardId, Long userId);

    Optional<User> findByKeycloakId(String keycloakId);

    List<User> findAllByIdIn(List<Long> ids);

    boolean existsByUsernameAndOrganizationId(String username, Long organizationId);

    boolean existsByEmailAndOrganizationId(String email, Long organizationId);

    List<User> findByEmailInAndOrganizationId(List<String> emails, Long organizationId);

    User findByEmailAndOrganizationId(String email, Long organizationId);

    @Query("SELECT u FROM User u " +
            "WHERE u.organization.id = :organizationId " +
            "AND LOWER(u.fullname) LIKE LOWER(CONCAT('%', :fullName, '%')) " +
            "AND u.id NOT IN (" +
            "   SELECT uw.user.id FROM UserWorkspace uw WHERE uw.workspace.id = :workspaceId" +
            ")")
    List<User> findByOrganizationAndFullNameNotInWorkspace(Long organizationId, String fullName, Long workspaceId);

    @Query("SELECT u FROM User u " +
            "WHERE u.organization.id = :organizationId " +
            "AND LOWER(u.fullname) LIKE LOWER(CONCAT('%', :fullName, '%')) " +
            "AND u.id IN (" +
            "   SELECT uw.user.id FROM UserWorkspace uw WHERE uw.workspace.id = :workspaceId" +
            ")")
    List<User> findByOrganizationAndFullNameInWorkspace(Long organizationId, String fullName, Long workspaceId);

    @Query("SELECT u FROM User u " +
            "WHERE u.organization.id = :organizationId " +
            "AND LOWER(u.fullname) LIKE LOWER(CONCAT('%', :fullName, '%')) " +
            "AND u.id NOT IN (" +
            "   SELECT ub.user.id FROM UserBoard ub WHERE ub.board.id = :boardId" +
            ")")
    List<User> findByOrganizationAndFullNameNotInBoard(Long organizationId, String fullName, Long boardId);

    List<User> findByIdIn(List<Long> ids);
}

