package com.xelops.actionplan.dto;

import java.util.List;

/**
 * Request body for POST /app-users/filter
 * All fields are optional – omitting them returns all users.
 *
 * - search : partial match on fullname OR email (case-insensitive)
 * - roles  : list of role names to include (e.g. ["ADMIN", "SIMPLE_USER"])
 */
public class UserFilterCriteriaDto {

    private String search;
    private List<String> roles;

    public UserFilterCriteriaDto() {}

    public UserFilterCriteriaDto(String search, List<String> roles) {
        this.search = search;
        this.roles = roles;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserFilterCriteriaDto{search='" + search + "', roles=" + roles + "}";
    }
}