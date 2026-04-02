package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByActionIdAndDeletedIsFalse(Long actionId);
}
