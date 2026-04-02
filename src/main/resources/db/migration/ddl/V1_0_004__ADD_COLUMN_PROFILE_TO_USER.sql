CREATE TYPE role_type AS ENUM ('SUPER_ADMIN','ADMIN', 'SIMPLE_USER');

ALTER TABLE "user"
    ADD COLUMN if not exists role role_type NOT NULL,
    ADD COLUMN if not exists username VARCHAR(255);

ALTER TABLE "user"
    DROP COLUMN if exists admin;

