package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.CheckList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckListRepository extends JpaRepository<CheckList, Long> {

    List<CheckList> findByActionId(Long actionId);

    void deleteByActionId(Long actionId);
}
