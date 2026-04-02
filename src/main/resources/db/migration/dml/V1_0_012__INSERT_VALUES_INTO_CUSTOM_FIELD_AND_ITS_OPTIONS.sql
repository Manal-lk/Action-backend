insert into custom_field(label, type, created_at)
values ('Priority', 'PRIORITY', now());

insert into custom_field_option(label, custom_field_id, created_at)
values ('Low', (select id from custom_field cf where cf.type = 'PRIORITY') , now()),
       ('Medium', (select id from custom_field cf where cf.type = 'PRIORITY'), now()),
       ('High', (select id from custom_field cf where cf.type = 'PRIORITY'), now());