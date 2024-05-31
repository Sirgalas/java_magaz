create schema if not exists catalogue;

create table if not exists catalogue.product(
    id serial primary key,
    title varchar(50) not null check(length(trim(title)) >= 3),
    detail text
);