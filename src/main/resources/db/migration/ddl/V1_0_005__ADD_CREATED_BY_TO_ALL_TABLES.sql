-- Migration to add created_by_id column to all tables
-- Assumes "user" table already exists and is referenced by created_by_id

ALTER TABLE organization ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE workspace ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE user_workspace ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE board ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE user_board ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE board_column ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE action ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE action_member ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE comment ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE attachment ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE checklist ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE checklist_item ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE tag ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE action_tag ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE custom_field ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE custom_field_option ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE custom_field_value ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);
ALTER TABLE user_invitation ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES "user"(id);

