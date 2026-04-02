ALTER TABLE if exists ATTACHMENT
    ADD COLUMN if not exists size BIGINT,
    ADD COLUMN if not exists storage_type VARCHAR(255),
    ALTER COLUMN type TYPE VARCHAR(255);