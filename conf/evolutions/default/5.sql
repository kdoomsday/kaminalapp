# --- !Ups

alter table users add column cambio_clave boolean not null default false;

update clientes set email = '' where email is null;

alter table clientes alter column email set not null;

create table pago_pendiente (
       id			      bigserial     not null primary key,
       id_mascota	  bigint        not null,
       monto		    numeric(16,2) not null,
       confirmacion	varchar(20),
       imagen			  varchar,

       constraint id_mascota_fk foreign key (id_mascota) references mascotas(id)
);

# --- !Downs

alter table users drop column cambio_clave;

alter table clientes alter column email drop not null;

drop table pago_pendiente;
