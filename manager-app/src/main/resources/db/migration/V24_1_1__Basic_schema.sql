create schema if not exists user_management;

create table if not exists user_management.user(
    id serial primary key,
    username varchar not null check(length(trim(username)) >= 0) unique,
    password varchar not null
);
create table if not exists user_management.authority(
    id serial primary key,
    authority varchar not null check(length(trim(authority)) >= 0) unique
);
create table if not exists user_management.user_authority(
    id serial primary key,
    user_id int not null references user_management.user(id),
    authority_id int not null references  user_management.user_authority(id),
    constraint uk_user_authority unique (user_id, authority_id)
);