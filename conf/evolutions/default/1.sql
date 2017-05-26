# --- !Ups
CREATE TABLE roles (
       id  serial PRIMARY KEY,
       name varchar(255) NOT NULL
);

CREATE TABLE users (
       id            bigserial    NOT NULL PRIMARY KEY,
       login         varchar(255) NOT NULL,
       password      varchar(255) NOT NULL,
       role_id       int NOT NULL REFERENCES roles(id),
       salt          int NOT NULL,

       connected     boolean NOT NULL DEFAULT false,
       last_activity timestamp,

       CONSTRAINT uq_login UNIQUE(login)
);

CREATE TABLE events (
       id           bigserial NOT NULL PRIMARY KEY,
       description  varchar   NOT NULL,
       moment       timestamp NOT NULL
);

CREATE TABLE clientes (
       id           bigserial NOT NULL PRIMARY KEY,
       nombre       varchar   NOT NULL
);

CREATE TABLE item (
       id           bigserial NOT NULL PRIMARY KEY,
       id_cliente   bigint    NOT NULL references clientes(id),
       monto        numeric(16, 2) NOT NULL,
       fecha        timestamp NOT NULL default(now())
);

insert into roles(name) values ('interno');
insert into roles(name) values ('usuario');

insert into users(login, password, salt, role_id)
values ('admin', 'c674d9cdee160ebec3ed9ec138ac473480054483f185be25c27e51f35f30175f', 42, (select id from roles where name='interno'));

# --- !Downs
drop table users;

drop table roles;

drop table events;

drop table item;

drop table clientes;
