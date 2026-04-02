package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.Action;
import com.xelops.actionplan.domain.BoardColumn;
import jakarta.persistence.Tuple;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {

    boolean existsById(@NotNull Long id);

    @Query("SELECT bc FROM BoardColumn bc WHERE bc.board.id = :boardId ORDER BY bc.offset ASC")
    List<BoardColumn> findAllByBoardIdOrderByOffset(Long boardId);

    @Query("SELECT bc.id FROM BoardColumn bc WHERE bc.board.id = :boardId")
    List<Long> findIdsByBoardId(Long boardId);

    @Query("SELECT CASE WHEN COUNT(bc) > 0 THEN true ELSE false END FROM BoardColumn bc WHERE bc.id = :columnId AND bc.board.id = :boardId")
    boolean inBoard(Long columnId, Long boardId);

    Optional<BoardColumn> findByIdAndBoardId(Long columnId, Long boardId);
}
