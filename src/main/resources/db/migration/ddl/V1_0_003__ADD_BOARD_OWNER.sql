ALTER TABLE board
    ADD COLUMN board_owner_id BIGSERIAL REFERENCES "user"(id);
