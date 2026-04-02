package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.UserBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBoardRepository extends JpaRepository<UserBoard, Long> {
    List<UserBoard> removeAllByBoard_Id(Long boardId);

    List<UserBoard> findAllByBoardId(Long boardId);
    boolean existsByUserIdAndBoardId(Long userId, Long boardId);

    Optional<UserBoard> findByUserIdAndBoardId(Long userId, Long boardId);
}
