drop table if exists users cascade;
drop table if exists posts cascade;
drop table if exists followers cascade;
drop table if exists comments cascade;

create table if not exists users (
    id bigint generated always as identity primary key not null,
    email varchar unique not null,
    username varchar not null,
    nickname varchar,
    password varchar not null,
    role varchar not null,
    status varchar not null,
    creation_date timestamp without time zone not null
);

create table if not exists posts (
    id bigint generated always as identity primary key not null,
    author_id bigint not null references users (id) on delete cascade,
    creation_date timestamp without time zone,
    description varchar not null,
    photo_url varchar not null
);

create table if not exists followers (
    id bigint generated by default as identity primary key,
    author_id bigint references users (id) on delete cascade,
    follower_id bigint references users (id) on delete cascade
);

create table if not exists comments (
    id bigint generated by default as identity primary key,
    text varchar not null,
    user_id bigint not null references users (id) on delete cascade,
    post_id bigint not null references posts (id) on delete cascade,
    creation_date timestamp without time zone not null
);

create table if not exists post_comments (
    post_id bigint not null references posts (id) on delete cascade,
    comment_id bigint not null references comments (id) on delete cascade
);