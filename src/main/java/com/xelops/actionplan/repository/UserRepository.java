package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

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

    // ── Utilisé par UserListService ───────────────────────────────────────────
    List<User> findByOrganizationId(Long organizationId);

    // ── User Management filter query ──────────────────────────────────────────

    /**
     * Returns all users of an organisation whose fullname matches the search
     * term (empty string = all) and whose role matches the given value.
     * If role is null all roles are included.
     */
    @Query("SELECT u FROM User u " +
            "WHERE u.organization.id = :organizationId " +
            "AND (:fullName IS NULL OR :fullName = '' OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "ORDER BY u.fullname ASC")
    List<User> findByOrganizationIdAndFilter(
            Long organizationId,
            String fullName,
            UserRoleEnum role
    );
}