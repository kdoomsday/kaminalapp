# --- !Ups

alter table users add column cambio_clave boolean not null default false;

# --- !Downs

alter table users drop column cambio_clave;
