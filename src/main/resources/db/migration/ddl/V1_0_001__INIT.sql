CREATE TABLE IF NOT EXISTS organization
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(255),
    type              VARCHAR(50), -- public / customer
    description       TEXT,
    realm             VARCHAR(255),
    logo              VARCHAR(255),
    primary_color     VARCHAR(50),
    secondary_color   VARCHAR(50),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workspace
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    description     TEXT,
    organization_id BIGSERIAL REFERENCES organization(id),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "user"
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255),
    fullname   VARCHAR(255),
    keycloakId VARCHAR(255),
    admin      BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_workspace
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGSERIAL REFERENCES "user"(id),
    workspace_id  BIGSERIAL REFERENCES workspace(id),
    profile       VARCHAR(50), -- member / administrator
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS board
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(255),
    description       TEXT,
    visibility        VARCHAR(50),
    active            BOOLEAN DEFAULT TRUE,
    background_image  VARCHAR(255),
    workspace_id      BIGSERIAL REFERENCES workspace(id),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_board
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGSERIAL REFERENCES "user"(id),
    board_id   BIGSERIAL REFERENCES board(id),
    profile    VARCHAR(50), -- member / administrator
    starred    BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS board_column
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255),
    "offset"   INTEGER,
    board_id   BIGSERIAL REFERENCES board(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS action
(
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255),
    description     TEXT,
    cover_image_url VARCHAR(255),
    start_date      DATE,
    due_date        DATE,
    status          VARCHAR(50), -- active / deleted / archived
    "offset"        INTEGER,
    board_column_id BIGSERIAL REFERENCES board_column(id),
    action_owner_id BIGSERIAL REFERENCES "user"(id),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS action_member
(
    id         BIGSERIAL PRIMARY KEY,
    action_id  BIGSERIAL REFERENCES action(id),
    user_id    BIGSERIAL REFERENCES "user"(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comment
(
    id         BIGSERIAL PRIMARY KEY,
    message    TEXT,
    action_id  BIGSERIAL REFERENCES action(id),
    user_id    BIGSERIAL REFERENCES "user"(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS attachment
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255),
    url        VARCHAR(255),
    type       VARCHAR(50), -- link / file
    action_id  BIGSERIAL REFERENCES action(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS checklist
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255),
    action_id  BIGSERIAL REFERENCES action(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS checklist_item
(
    id           BIGSERIAL PRIMARY KEY,
    description  VARCHAR(255),
    checked      BOOLEAN DEFAULT FALSE,
    checklist_id BIGSERIAL REFERENCES checklist(id),
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tag
(
    id         BIGSERIAL PRIMARY KEY,
    label      VARCHAR(255),
    color_code VARCHAR(50),
    board_id   BIGSERIAL REFERENCES board(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS action_tag
(
    id         BIGSERIAL PRIMARY KEY,
    action_id  BIGSERIAL REFERENCES action(id),
    tag_id     BIGSERIAL REFERENCES tag(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS custom_field
(
    id         BIGSERIAL PRIMARY KEY,
    label      VARCHAR(255),
    type       VARCHAR(50), -- text / number / select / date
    board_id   BIGSERIAL REFERENCES board(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
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
    value           TEXT,
    action_id       BIGSERIAL REFERENCES action(id),
    custom_field_id BIGSERIAL REFERENCES custom_field(id),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_invitation
(
    id           BIGSERIAL PRIMARY KEY,
    email        VARCHAR(255),
    status       VARCHAR(50), -- pending / accepted / expired
    expires_at   TIMESTAMP,
    workspace_id BIGSERIAL REFERENCES workspace(id),
    board_id     BIGSERIAL REFERENCES board(id),
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP
);
