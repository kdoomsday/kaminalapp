# --- !Ups

alter table clientes add column apellido varchar;

update clientes set apellido = '' where apellido is null;

alter table clientes alter column apellido set not null;

alter table clientes add column direccion varchar;

alter table clientes add column email varchar;

create table mascotas (
       id           bigserial not null primary key,
       nombre       varchar not null,
       raza         varchar,
       edad         int check(edad >= 0),
       fecha_inicio timestamp,
       id_cliente   bigint not null references clientes(id)
);

# --- !Downs

alter table clientes drop column apellido;

alter table clientes drop column direccion;

alter table clientes drop column email;

drop table mascotas;
