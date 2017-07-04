# --- !Ups
create table servicio (
    id         bigserial primary key not null,

    nombre     varchar not null constraint uk_servicio_nombre UNIQUE,
    precio     numeric(16,2) not null,
    mensual    boolean not null default false
);

-- Mover referencia de cliente a mascota
truncate table item restart identity;

alter table item add column id_mascota bigint not null;

alter table item add constraint item_mascota_fk
                 foreign key (id_mascota) references mascotas(id);

alter table item drop column id_cliente;


# --- !Downs

drop table servicio;

alter table item drop column id_mascota;

alter table item add column id_cliente bigint not null references clientes(id)
