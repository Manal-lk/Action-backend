-- workspace table
ALTER TABLE workspace
    ALTER COLUMN organization_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS workspace_organization_id_seq;
ALTER TABLE workspace
    ALTER COLUMN organization_id TYPE BIGINT;

-- user_workspace table
ALTER TABLE user_workspace
    ALTER COLUMN user_id DROP DEFAULT,
    ALTER COLUMN workspace_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS user_workspace_user_id_seq;
DROP SEQUENCE IF EXISTS user_workspace_workspace_id_seq;
ALTER TABLE user_workspace
    ALTER COLUMN user_id TYPE BIGINT,
    ALTER COLUMN workspace_id TYPE BIGINT;

-- board table
ALTER TABLE board
    ALTER COLUMN workspace_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS board_workspace_id_seq;
ALTER TABLE board
    ALTER COLUMN workspace_id TYPE BIGINT;

-- user_board table
ALTER TABLE user_board
    ALTER COLUMN user_id DROP DEFAULT,
    ALTER COLUMN board_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS user_board_user_id_seq;
DROP SEQUENCE IF EXISTS user_board_board_id_seq;
ALTER TABLE user_board
    ALTER COLUMN user_id TYPE BIGINT,
    ALTER COLUMN board_id TYPE BIGINT;

-- board_column table
ALTER TABLE board_column
    ALTER COLUMN board_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS board_column_board_id_seq;
ALTER TABLE board_column
    ALTER COLUMN board_id TYPE BIGINT;

-- action table
ALTER TABLE action
    ALTER COLUMN board_column_id DROP DEFAULT,
    ALTER COLUMN owner_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS action_board_column_id_seq;
DROP SEQUENCE IF EXISTS action_owner_id_seq;
ALTER TABLE action
    ALTER COLUMN board_column_id TYPE BIGINT,
    ALTER COLUMN owner_id TYPE BIGINT;

-- action_member table
ALTER TABLE action_member
    ALTER COLUMN action_id DROP DEFAULT,
    ALTER COLUMN user_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS action_member_action_id_seq;
DROP SEQUENCE IF EXISTS action_member_user_id_seq;
ALTER TABLE action_member
    ALTER COLUMN action_id TYPE BIGINT,
    ALTER COLUMN user_id TYPE BIGINT;

-- comment table
ALTER TABLE comment
    ALTER COLUMN action_id DROP DEFAULT,
    ALTER COLUMN user_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS comment_action_id_seq;
DROP SEQUENCE IF EXISTS comment_user_id_seq;
ALTER TABLE comment
    ALTER COLUMN action_id TYPE BIGINT,
    ALTER COLUMN user_id TYPE BIGINT;

-- attachment table
ALTER TABLE attachment
    ALTER COLUMN action_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS attachment_action_id_seq;
ALTER TABLE attachment
    ALTER COLUMN action_id TYPE BIGINT;

-- checklist table
ALTER TABLE checklist
    ALTER COLUMN action_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS checklist_action_id_seq;
ALTER TABLE checklist
    ALTER COLUMN action_id TYPE BIGINT;

-- checklist_item table
ALTER TABLE checklist_item
    ALTER COLUMN checklist_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS checklist_item_checklist_id_seq;
ALTER TABLE checklist_item
    ALTER COLUMN checklist_id TYPE BIGINT;

-- tag table
ALTER TABLE tag
    ALTER COLUMN board_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS tag_board_id_seq;
ALTER TABLE tag
    ALTER COLUMN board_id TYPE BIGINT;

-- action_tag table
ALTER TABLE action_tag
    ALTER COLUMN action_id DROP DEFAULT,
    ALTER COLUMN tag_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS action_tag_action_id_seq;
DROP SEQUENCE IF EXISTS action_tag_tag_id_seq;
ALTER TABLE action_tag
    ALTER COLUMN action_id TYPE BIGINT,
    ALTER COLUMN tag_id TYPE BIGINT;

-- custom_field table
ALTER TABLE custom_field
    ALTER COLUMN board_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS custom_field_board_id_seq;
ALTER TABLE custom_field
    ALTER COLUMN board_id TYPE BIGINT;

-- custom_field_option table
ALTER TABLE custom_field_option
    ALTER COLUMN custom_field_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS custom_field_option_custom_field_id_seq;
ALTER TABLE custom_field_option
    ALTER COLUMN custom_field_id TYPE BIGINT;

-- custom_field_value table
ALTER TABLE custom_field_value
    ALTER COLUMN action_id DROP DEFAULT,
    ALTER COLUMN custom_field_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS custom_field_value_action_id_seq;
DROP SEQUENCE IF EXISTS custom_field_value_custom_field_id_seq;
ALTER TABLE custom_field_value
    ALTER COLUMN action_id TYPE BIGINT,
    ALTER COLUMN custom_field_id TYPE BIGINT;

-- user_invitation table
ALTER TABLE user_invitation
    ALTER COLUMN workspace_id DROP DEFAULT,
    ALTER COLUMN board_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS user_invitation_workspace_id_seq;
DROP SEQUENCE IF EXISTS user_invitation_board_id_seq;
ALTER TABLE user_invitation
    ALTER COLUMN workspace_id TYPE BIGINT,
    ALTER COLUMN board_id TYPE BIGINT;

