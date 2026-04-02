alter table if exists custom_field
    alter column board_id drop default;

alter table if exists custom_field
    alter column board_id drop NOT NULL;

alter table if exists custom_field
    alter column board_id type bigint;

drop sequence if exists custom_field_board_id_seq;