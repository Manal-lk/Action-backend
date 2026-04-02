package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.CustomField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {
    Optional<CustomField> findByType(String type);
}
