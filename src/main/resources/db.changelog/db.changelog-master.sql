-- liquibase formatted sql

--changeset user:01 onFail:WARN
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE if not exists books
(
    id         uuid primary key default gen_random_uuid(),
    name       varchar(500)     not null,
    author     varchar(255)     not null,
    created_at timestamp        not null default (current_timestamp),
    updated_at timestamp        not null default (current_timestamp)
);

create table if not exists objective_themes
(
    id         uuid primary key   default gen_random_uuid(),
    space_id   int       not null,
    human_id   serial    not null,
    "name"     varchar   not null,
    color_code varchar   not null,
    created_at timestamp not null default (current_timestamp),
    updated_at timestamp not null default (current_timestamp)
);
