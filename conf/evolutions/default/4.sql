# --- !Ups
create table servicio (
    id         bigserial primary key not null,

    nombre     varchar not null constraint uk_servicio_nombre UNIQUE,
    precio     numeric(16,2) not null,
    mensual    boolean not null default false
);

# --- !Downs

drop table servicio;
