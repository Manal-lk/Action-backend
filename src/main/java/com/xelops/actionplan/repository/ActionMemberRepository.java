package com.xelops.actionplan.repository;


import com.xelops.actionplan.domain.ActionMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionMemberRepository extends JpaRepository<ActionMember, Long> {

    @Query("SELECT am.id FROM ActionMember am WHERE am.action.id = :actionId")
    List<Long> findAllIdsByActionId(Long actionId);

    List<ActionMember> findAllByActionId(Long actionId);
}
