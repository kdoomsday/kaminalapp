# --- !Ups

alter table clientes add column apellido varchar;

update clientes set apellido = '' where apellido is null;

alter table clientes alter column apellido set not null;

alter table clientes add column direccion varchar;

alter table clientes add column email varchar;


# --- !Downs

alter table clientes drop column apellido;

alter table clientes drop column direccion;

alter table clientes drop column email;
