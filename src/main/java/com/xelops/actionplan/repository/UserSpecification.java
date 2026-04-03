package com.xelops.actionplan.repository;

import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.UserFilterCriteriaDto;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> withFilters(UserFilterCriteriaDto criteria, Long organizationId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // toujours filtrer par organisation (multi-tenancy)
            predicates.add(cb.equal(root.get("organization").get("id"), organizationId));

            // filtre texte: fullname OR email
            if (criteria.getSearch() != null && !criteria.getSearch().isBlank()) {
                String pattern = "%" + criteria.getSearch().toLowerCase() + "%";
                Predicate namePred = cb.like(cb.lower(root.get("fullname")), pattern);
                Predicate emailPred = cb.like(cb.lower(root.get("email")), pattern);
                predicates.add(cb.or(namePred, emailPred));
            }

            // filtre rôle (multi-select)
            if (criteria.getRoles() != null && !criteria.getRoles().isEmpty()) {
                predicates.add(root.get("role").in(criteria.getRoles()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}