ALTER TABLE user_invitation
    ALTER COLUMN workspace_id DROP NOT NULL,
    ALTER COLUMN board_id DROP NOT NULL;