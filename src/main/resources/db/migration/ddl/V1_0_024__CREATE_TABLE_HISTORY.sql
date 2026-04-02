CREATE TABLE if not exists history
(
    id                    BIGSERIAL PRIMARY KEY,
    concerned_user_id     BIGINT REFERENCES "user"(id),
    old_concerned_user_id BIGINT REFERENCES "user"(id),
    source_column_id      BIGINT REFERENCES board_column (id),
    target_column_id      BIGINT REFERENCES board_column (id),
    action_history_type   VARCHAR(255),
    old_data              TEXT,
    new_data              TEXT,
    action_id             BIGINT REFERENCES action (id),

    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP,
    created_by_id             BIGINT REFERENCES "user"(id)
);