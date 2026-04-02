alter table action
    add column priority_id BIGINT references custom_field_option(id);