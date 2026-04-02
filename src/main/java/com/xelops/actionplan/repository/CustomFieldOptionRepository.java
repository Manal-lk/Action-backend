package com.xelops.actionplan.repository;


import com.xelops.actionplan.domain.CustomFieldOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldOptionRepository extends JpaRepository<CustomFieldOption, Long> {

    List<CustomFieldOption> findAllByCustomField_TypeOrderByLabel(String type);
    
    Optional<CustomFieldOption> findByLabelAndCustomField_Type(String label, String type);
}
