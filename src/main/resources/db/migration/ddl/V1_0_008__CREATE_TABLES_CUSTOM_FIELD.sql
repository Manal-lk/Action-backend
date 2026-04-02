CREATE TABLE IF NOT EXISTS custom_field
(
    id              BIGSERIAL PRIMARY KEY,
    label           VARCHAR(255),
    type            VARCHAR(50),
    board_id BIGSERIAL REFERENCES board(id),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS custom_field_option
(
    id              BIGSERIAL PRIMARY KEY,
    label           VARCHAR(255),
    custom_field_id BIGSERIAL REFERENCES custom_field(id),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS custom_field_value
(
    id              BIGSERIAL PRIMARY KEY,
    value           VARCHAR(255),
    custom_field_id BIGSERIAL REFERENCES custom_field(id),
    action_id       BIGSERIAL REFERENCES action(id),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP
);