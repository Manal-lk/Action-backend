ALTER TABLE if exists ACTION
    ADD COLUMN if not exists completed BOOLEAN DEFAULT FALSE;
