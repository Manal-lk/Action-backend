ALTER TABLE if exists action
    RENAME COLUMN owner_id to assignee_id;

ALTER TABLE if exists action_member
    RENAME COLUMN user_id TO member_id;

