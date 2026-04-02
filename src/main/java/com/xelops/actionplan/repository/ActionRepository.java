package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.Action;
import com.xelops.actionplan.projections.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {

    boolean existsByTitleAndBoardColumn_Id(String title, Long boardColumnId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Action a WHERE a.id = :actionId and a.boardColumn.board.id = :boardId")
    boolean inBoard(Long actionId, Long boardId);

    List<Action> findAllByBoardColumn_IdOrderByOffset(Long boardColumnId);

    @Query("""
                SELECT a FROM Action a
                WHERE a.boardColumn.board.id = :boardId
                    AND (:columnId IS NULL OR a.boardColumn.id = :columnId)
                    AND (:#{#assigneeIds == null || #assigneeIds.isEmpty()} = true OR a.assignee.id IN :assigneeIds)
                    AND (:#{#startDueDate == null} = true OR a.dueDate >= :startDueDate)
                    AND (:#{#endDueDate == null} = true OR a.dueDate <= :endDueDate)
                    AND (:#{#completed == null} = true OR a.completed = :completed)
                ORDER BY a.boardColumn.id ASC, a.offset ASC
            """)
    List<Action> findAllByBoardIdAndFilterOptions(
            Long boardId,
            Long columnId,
            List<Long> assigneeIds,
            LocalDate startDueDate,
            LocalDate endDueDate,
            Boolean completed
    );

    @Query("""
                SELECT COUNT(a) as total,
                    COUNT(CASE WHEN a.completed = true THEN 1 END) as completed,
                    COUNT(CASE WHEN a.completed = false AND a.dueDate IS NOT NULL AND a.dueDate < CURRENT_DATE THEN 1 END) as overdue,
                    CASE WHEN COUNT(a) = 0 THEN 0.0 ELSE 1.0 * COUNT(CASE WHEN a.completed = true THEN 1 END) / COUNT(a)
                    END as completionRate
                FROM Action a
                LEFT JOIN a.members am
                WHERE (:isAdmin = true OR a.assignee.id = :userId OR :userId = am.member.id)
                    AND a.boardColumn.board.workspace.organization.id = :organizationId
            """)
    ActionStatisticsProjection getActionStatisticsProjection(Long userId, boolean isAdmin, Long organizationId);

    @Query("""
                SELECT CASE WHEN COUNT(a) = 0 THEN 0.0 ELSE 100.0 * COUNT(CASE WHEN a.completed = true THEN 1 END) / COUNT(a) END as completedRate,
                    COUNT(CASE WHEN a.completed = true THEN 1 END) as completedCount,
                    CASE WHEN COUNT(a) = 0 THEN 0.0 ELSE 100.0 * COUNT(CASE WHEN a.completed = false THEN 1 END) / COUNT(a) END as openRate,
                    COUNT(CASE WHEN a.completed = false THEN 1 END) as openCount
                FROM Action a
                LEFT JOIN a.members am
                WHERE ( :isAdmin = true OR a.assignee.id = :userId OR :userId = am.member.id )
                  AND a.boardColumn.board.workspace.organization.id = :organizationId
            """)
    ActionStatusBreakdownProjection getActionStatusBreakdown(Long userId, boolean isAdmin, Long organizationId);

    @Query(value = """
            SELECT
                COUNT(*) FILTER (WHERE a.completed = false 
                    AND a.due_date IS NOT NULL
                    AND CURRENT_DATE - a.due_date BETWEEN 1 AND 3) AS oneToThreeDays,
            
                COUNT(*) FILTER (WHERE a.completed = false 
                    AND a.due_date IS NOT NULL
                    AND CURRENT_DATE - a.due_date BETWEEN 4 AND 7) AS fourToSevenDays,
            
                COUNT(*) FILTER (WHERE a.completed = false 
                    AND a.due_date IS NOT NULL
                    AND CURRENT_DATE - a.due_date BETWEEN 8 AND 14) AS eightToFourteenDays,
            
                COUNT(*) FILTER (WHERE a.completed = false 
                    AND a.due_date IS NOT NULL
                    AND CURRENT_DATE - a.due_date > 14) AS fifteenPlusDays
            
            FROM action_plan.action a
            LEFT JOIN action_plan.action_member am ON am.action_id = a.id
            LEFT JOIN board_column bc ON a.board_column_id = bc.id
            LEFT JOIN board b ON bc.board_id = b.id
            LEFT JOIN workspace w ON b.workspace_id = w.id
            WHERE (:isAdmin = true OR a.assignee_id = :userId OR am.member_id = :userId)
            AND w.organization_id = :organizationId
            """, nativeQuery = true)
    OverdueActionsBreakdownProjection getOverdueActionsBreakdown(
            Long userId,
            boolean isAdmin,
            Long organizationId
    );

    @Query(value = """
            SELECT AVG((a.completion_date::date - a.created_at::date)) as averageResolutionTime
                FROM action a
                LEFT JOIN action_member am ON a.id = am.action_id
                left join board_column bc on a.board_column_id = bc.id
                left join board b on b.id = bc.board_id
                left join workspace w on b.workspace_id = w.id
                WHERE a.completed = true AND a.completion_date IS NOT NULL AND a.created_at IS NOT null
                        AND w.organization_id  = :organizationId""", nativeQuery = true)
    Double getAverageResolutionTimeNative(Long userId, boolean isAdmin, Long organizationId);

    @Query("""
                SELECT u.fullname as assigneeName, COUNT(a.id) as completedCount
                FROM Action a
                JOIN a.assignee u
                WHERE a.completed = true
                  AND a.boardColumn.board.workspace.organization.id = :organizationId
                GROUP BY u.fullname
                ORDER BY completedCount DESC
                LIMIT 5
            """)
    List<TopAssigneeProjection> getTopAssignees(Long organizationId);

    @Query(value = """
        SELECT 
            a.title AS actionName,
            (a.due_date - CURRENT_DATE) AS daysRemaining
        FROM action_plan.action a
        LEFT JOIN action_plan.board_column bc ON a.board_column_id = bc.id
        LEFT JOIN action_plan.board b ON bc.board_id = b.id
        LEFT JOIN action_plan.workspace w ON b.workspace_id = w.id
        WHERE a.completed = false
          AND a.due_date IS NOT NULL
          AND a.due_date >= CURRENT_DATE
          AND a.due_date <= :limitDate
          AND w.organization_id = :organizationId
        ORDER BY a.due_date ASC
        LIMIT 5
        """, nativeQuery = true)
    List<UpcomingDeadlineProjection> getUpcomingDeadlines(Long organizationId, LocalDate limitDate);

    @Query("""
                SELECT p.label as priority,
                    CASE WHEN COUNT(a.id) = 0 THEN 0.0
                         ELSE 100.0 * COUNT(CASE WHEN a.completed = true THEN 1 END) / COUNT(a.id)
                    END as completionRate,
                    COUNT(a) as total,
                    COUNT(CASE WHEN a.completed = true THEN 1 END) as completed
                FROM Action a
                JOIN a.priority p
                WHERE a.boardColumn.board.workspace.organization.id = :organizationId
                GROUP BY p.id, p.label
                HAVING LOWER(p.label) IN ('high', 'medium', 'low')
                ORDER BY p.id
            """)
    List<CompletionRateByPriorityProjection> getCompletionRateByPriority(Long organizationId);

    @Query("""
                SELECT EXTRACT(YEAR FROM a.createdAt) as year,
                       EXTRACT(MONTH FROM a.createdAt) as month,
                       COUNT(a.id) as createdCount,
                       COUNT(CASE WHEN a.completed = true THEN 1 END) as completedCount
                FROM Action a
                WHERE a.boardColumn.board.workspace.organization.id = :organizationId
                GROUP BY EXTRACT(YEAR FROM a.createdAt), EXTRACT(MONTH FROM a.createdAt)
                ORDER BY EXTRACT(YEAR FROM a.createdAt), EXTRACT(MONTH FROM a.createdAt)
            """)
    List<ActionsTrendProjection> getActionsTrend(Long organizationId);
}
