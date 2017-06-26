# --- !Ups
alter table clientes add column cuenta char(20);

ALTER TABLE clientes DROP CONSTRAINT clientes_nombre_key;

create table telefonos (
       numero          varchar(30) not null,
       id_cliente      bigint      not null references clientes(id),
       primary key (numero, id_cliente)
);

# --- !Downs

alter table clientes drop column cuenta;

ALTER TABLE clientes ADD CONSTRAINT clientes_nombre_key UNIQUE(nombre);

drop table telefonos;
