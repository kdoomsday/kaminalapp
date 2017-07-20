# --- !Ups

alter table users add column cambio_clave boolean not null default false;

update clientes set email = '' where email is null;

alter table clientes alter column email set not null;

# --- !Downs

alter table users drop column cambio_clave;

alter table clientes alter column email drop not null;
